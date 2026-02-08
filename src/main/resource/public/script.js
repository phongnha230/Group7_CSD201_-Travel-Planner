// ========================================
// Travel Planner Pro - JavaScript
// ========================================

// Global state
let locations = [];
let edges = [];
let currentPath = [];
let customers = [];
let activityLogs = [];

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
    initNavigation();
    initMapTab();
    initTourTab();
    initCustomersTab();
    addLog('info', 'System initialized successfully');
});

// ========================================
// Navigation
// ========================================
function initNavigation() {
    const navTabs = document.querySelectorAll('.nav-tab');
    const tabContents = document.querySelectorAll('.tab-content');
    
    navTabs.forEach(tab => {
        tab.addEventListener('click', () => {
            // Remove active from all
            navTabs.forEach(t => t.classList.remove('active'));
            tabContents.forEach(c => c.classList.remove('active'));
            
            // Add active to clicked
            tab.classList.add('active');
            const tabId = tab.getAttribute('data-tab') + '-tab';
            document.getElementById(tabId).classList.add('active');
        });
    });
}

// ========================================
// Map Tab (Graph - Dijkstra)
// ========================================
function initMapTab() {
    fetch('/api/locations')
        .then(res => res.json())
        .then(data => {
            locations = data.locations;
            edges = data.edges;
            populateDropdowns();
            drawMap();
        })
        .catch(err => console.error('Error loading locations:', err));
    
    document.getElementById('findBtn').addEventListener('click', findPath);
    
    // Zoom controls
    document.getElementById('zoomInBtn').addEventListener('click', () => {
        mapScale = Math.min(1.8, mapScale + 0.15);
        drawMap();
    });
    document.getElementById('zoomOutBtn').addEventListener('click', () => {
        mapScale = Math.max(0.5, mapScale - 0.15);
        drawMap();
    });
    document.getElementById('resetViewBtn').addEventListener('click', () => {
        mapScale = 1;
        mapOffsetX = 0;
        mapOffsetY = 0;
        drawMap();
    });
    
    // Criteria buttons
    document.querySelectorAll('.criteria-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.criteria-btn').forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
        });
    });
    
    // Swap button
    document.getElementById('swapBtn').addEventListener('click', () => {
        const startSelect = document.getElementById('startPoint');
        const endSelect = document.getElementById('endPoint');
        const temp = startSelect.value;
        startSelect.value = endSelect.value;
        endSelect.value = temp;
    });
}

function populateDropdowns() {
    const startSelect = document.getElementById('startPoint');
    const endSelect = document.getElementById('endPoint');
    const tourSelect = document.getElementById('tourLocationSelect');
    
    locations.forEach(loc => {
        const opt1 = new Option(loc.name, loc.id);
        const opt2 = new Option(loc.name, loc.id);
        const opt3 = new Option(loc.name, loc.id);
        startSelect.add(opt1);
        endSelect.add(opt2);
        if (tourSelect) tourSelect.add(opt3);
    });
}

// Map zoom/pan state
let mapScale = 1;
let mapOffsetX = 0;
let mapOffsetY = 0;

// Cross-browser rounded rectangle
function roundRect(ctx, x, y, w, h, r) {
    ctx.beginPath();
    ctx.moveTo(x + r, y);
    ctx.lineTo(x + w - r, y);
    ctx.quadraticCurveTo(x + w, y, x + w, y + r);
    ctx.lineTo(x + w, y + h - r);
    ctx.quadraticCurveTo(x + w, y + h, x + w - r, y + h);
    ctx.lineTo(x + r, y + h);
    ctx.quadraticCurveTo(x, y + h, x, y + h - r);
    ctx.lineTo(x, y + r);
    ctx.quadraticCurveTo(x, y, x + r, y);
    ctx.closePath();
}

// Transform coordinates to spread nodes across canvas (dãn bản đồ)
function getSpreadCoords(loc) {
    if (locations.length < 2) return { x: loc.x, y: loc.y };
    const xs = locations.map(l => l.x);
    const ys = locations.map(l => l.y);
    const minX = Math.min(...xs), maxX = Math.max(...xs);
    const minY = Math.min(...ys), maxY = Math.max(...ys);
    const rangeX = maxX - minX || 1;
    const rangeY = maxY - minY || 1;
    const padding = 120;
    const canvasW = 900, canvasH = 600;
    const availW = canvasW - padding * 2;
    const availH = canvasH - padding * 2;
    const x = padding + ((loc.x - minX) / rangeX) * availW;
    const y = padding + ((loc.y - minY) / rangeY) * availH;
    return { x, y };
}

function drawMap() {
    const canvas = document.getElementById('mapCanvas');
    const ctx = canvas.getContext('2d');
    
    const scale = mapScale;
    const offsetX = mapOffsetX;
    const offsetY = mapOffsetY;
    
    // --- Background: gradient + subtle grid ---
    const bgGradient = ctx.createLinearGradient(0, 0, canvas.width, canvas.height);
    bgGradient.addColorStop(0, '#0c1929');
    bgGradient.addColorStop(0.5, '#132238');
    bgGradient.addColorStop(1, '#0f172a');
    ctx.fillStyle = bgGradient;
    ctx.fillRect(0, 0, canvas.width, canvas.height);
    
    // Grid pattern
    ctx.strokeStyle = 'rgba(71, 85, 105, 0.15)';
    ctx.lineWidth = 0.5;
    const gridSize = 40;
    for (let gx = 0; gx <= canvas.width; gx += gridSize) {
        ctx.beginPath();
        ctx.moveTo(gx, 0);
        ctx.lineTo(gx, canvas.height);
        ctx.stroke();
    }
    for (let gy = 0; gy <= canvas.height; gy += gridSize) {
        ctx.beginPath();
        ctx.moveTo(0, gy);
        ctx.lineTo(canvas.width, gy);
        ctx.stroke();
    }
    
    // --- Draw edges first ---
    edges.forEach(edge => {
        const start = getLocationById(edge.start);
        const end = getLocationById(edge.end);
        if (start && end) {
            const isInPath = isEdgeInPath(edge);
            const p1 = getSpreadCoords(start);
            const p2 = getSpreadCoords(end);
            const x1 = p1.x * scale + offsetX;
            const y1 = p1.y * scale + offsetY;
            const x2 = p2.x * scale + offsetX;
            const y2 = p2.y * scale + offsetY;
            
            // Curved edge for softer look
            const midX = (x1 + x2) / 2;
            const midY = (y1 + y2) / 2;
            const ctrlOffset = Math.min(30, Math.hypot(x2 - x1, y2 - y1) * 0.15);
            const ctrlX = midX + (y1 - y2) * 0.1 + ctrlOffset;
            const ctrlY = midY + (x2 - x1) * 0.1 - ctrlOffset;
            
            ctx.beginPath();
            ctx.moveTo(x1, y1);
            ctx.quadraticCurveTo(ctrlX, ctrlY, x2, y2);
            
            if (isInPath) {
                ctx.shadowBlur = 18;
                ctx.shadowColor = '#38bdf8';
                ctx.strokeStyle = 'rgba(56, 189, 248, 0.9)';
                ctx.lineWidth = 4;
            } else {
                ctx.shadowBlur = 0;
                ctx.strokeStyle = 'rgba(94, 234, 212, 0.25)';
                ctx.lineWidth = 2;
            }
            ctx.stroke();
            
            // Weight label - pill badge
            const labelText = edge.weight.toString();
            const textWidth = ctx.measureText(labelText).width;
            const padX = 12;
            const padY = 10;
            const labelW = textWidth + padX * 2;
            const labelH = 22;
            const labelX = midX - labelW / 2;
            const labelY = midY - labelH / 2;
            
            ctx.shadowBlur = 0;
            
            // Pill background
            ctx.beginPath();
            roundRect(ctx, labelX, labelY, labelW, labelH, 12);
            ctx.fillStyle = isInPath 
                ? 'rgba(56, 189, 248, 0.25)' 
                : 'rgba(30, 41, 59, 0.85)';
            ctx.fill();
            ctx.strokeStyle = isInPath ? '#38bdf8' : 'rgba(148, 163, 184, 0.4)';
            ctx.lineWidth = 1;
            ctx.stroke();
            
            ctx.fillStyle = isInPath ? '#7dd3fc' : '#cbd5e1';
            ctx.font = '600 13px Inter';
            ctx.textAlign = 'center';
            ctx.textBaseline = 'middle';
            ctx.fillText(labelText, midX, midY);
        }
    });
    
    // --- Draw nodes ---
    locations.forEach(loc => {
        const pathIndex = currentPath.findIndex(p => p.id === loc.id);
        const isInPath = pathIndex >= 0;
        const isStart = pathIndex === 0;
        const isEnd = pathIndex === currentPath.length - 1;
        
        const pos = getSpreadCoords(loc);
        const x = pos.x * scale + offsetX;
        const y = pos.y * scale + offsetY;
        
        // Outer glow
        if (isInPath) {
            const glowGrad = ctx.createRadialGradient(x, y, 8, x, y, 35);
            glowGrad.addColorStop(0, isStart ? 'rgba(34, 197, 94, 0.4)' : isEnd ? 'rgba(239, 68, 68, 0.4)' : 'rgba(56, 189, 248, 0.35)');
            glowGrad.addColorStop(0.7, 'rgba(56, 189, 248, 0.08)');
            glowGrad.addColorStop(1, 'transparent');
            ctx.fillStyle = glowGrad;
            ctx.beginPath();
            ctx.arc(x, y, 35, 0, Math.PI * 2);
            ctx.fill();
        }
        
        // Node ring
        ctx.beginPath();
        ctx.arc(x, y, 22, 0, Math.PI * 2);
        
        if (isInPath) {
            ctx.shadowBlur = 12;
            ctx.shadowColor = isStart ? '#22c55e' : isEnd ? '#ef4444' : '#38bdf8';
            const ringGrad = ctx.createRadialGradient(x - 5, y - 5, 0, x, y, 22);
            ringGrad.addColorStop(0, isStart ? '#4ade80' : isEnd ? '#f87171' : '#7dd3fc');
            ringGrad.addColorStop(0.7, isStart ? '#22c55e' : isEnd ? '#ef4444' : '#0ea5e9');
            ringGrad.addColorStop(1, isStart ? '#16a34a' : isEnd ? '#dc2626' : '#0284c7');
            ctx.fillStyle = ringGrad;
            ctx.fill();
            ctx.strokeStyle = isStart ? '#4ade80' : isEnd ? '#f87171' : '#38bdf8';
            ctx.lineWidth = 3;
            ctx.stroke();
        } else {
            ctx.shadowBlur = 0;
            const ringGrad = ctx.createRadialGradient(x - 5, y - 5, 0, x, y, 22);
            ringGrad.addColorStop(0, 'rgba(148, 163, 184, 0.5)');
            ringGrad.addColorStop(0.8, 'rgba(71, 85, 105, 0.4)');
            ringGrad.addColorStop(1, 'rgba(51, 65, 85, 0.5)');
            ctx.fillStyle = ringGrad;
            ctx.fill();
            ctx.strokeStyle = 'rgba(148, 163, 184, 0.5)';
            ctx.lineWidth = 2;
            ctx.stroke();
        }
        
        // Inner dot
        ctx.beginPath();
        ctx.arc(x, y, 10, 0, Math.PI * 2);
        ctx.fillStyle = isInPath ? '#ffffff' : 'rgba(203, 213, 225, 0.9)';
        ctx.fill();
        
        // City name badge
        ctx.shadowBlur = 0;
        ctx.font = '600 13px Inter';
        ctx.textAlign = 'center';
        const nameWidth = ctx.measureText(loc.name).width;
        const badgeW = nameWidth + 20;
        const badgeH = 24;
        const badgeX = x - badgeW / 2;
        const badgeY = y + 28;
        
        ctx.beginPath();
        roundRect(ctx, badgeX, badgeY, badgeW, badgeH, 12);
        ctx.fillStyle = isInPath ? 'rgba(30, 41, 59, 0.9)' : 'rgba(15, 23, 42, 0.85)';
        ctx.fill();
        ctx.strokeStyle = isInPath ? 'rgba(56, 189, 248, 0.5)' : 'rgba(148, 163, 184, 0.3)';
        ctx.lineWidth = 1;
        ctx.stroke();
        
        ctx.fillStyle = '#f1f5f9';
        ctx.fillText(loc.name, x, badgeY + badgeH / 2);
        
        // Start/End labels
        if (isStart) {
            ctx.font = '700 10px Inter';
            ctx.fillStyle = '#22c55e';
            ctx.fillText('START', x, y - 30);
        } else if (isEnd) {
            ctx.font = '700 10px Inter';
            ctx.fillStyle = '#ef4444';
            ctx.fillText('END', x, y - 30);
        }
    });
}

function findPath() {
    const start = document.getElementById('startPoint').value;
    const end = document.getElementById('endPoint').value;
    
    if (!start || !end) {
        alert('Please select both starting and destination cities');
        return;
    }
    
    if (start === end) {
        const startName = locations.find(l => l.id === start)?.name || 'Unknown';
        currentPath = [{ id: start, name: startName }];
        drawMap();
        const resultsPanel = document.getElementById('resultsPanel');
        resultsPanel.classList.remove('hidden');
        document.getElementById('totalDistance').innerHTML = `0 <small>km</small>`;
        document.getElementById('estTime').innerHTML = `0 <small>hrs</small>`;
        document.getElementById('pathList').innerHTML = `
            <div class="path-item">
                <div class="path-dot start"></div>
                <div class="path-info">
                    <div class="path-name">${startName}</div>
                    <div class="path-detail">Điểm xuất phát trùng với điểm đến (0 km)</div>
                </div>
            </div>
        `;
        return;
    }
    
    fetch(`/api/find-path?start=${start}&end=${end}`)
        .then(res => res.json())
        .then(path => {
            if (path.error) {
                alert(path.error);
                return;
            }
            
            currentPath = path;
            drawMap();
            
            // Show results panel
            const resultsPanel = document.getElementById('resultsPanel');
            resultsPanel.classList.remove('hidden');
            
            // Calculate total distance (km)
            let totalDistance = 0;
            for (let i = 0; i < path.length - 1; i++) {
                const edge = edges.find(e => 
                    (e.start === path[i].id && e.end === path[i+1].id) ||
                    (e.start === path[i+1].id && e.end === path[i].id)
                );
                if (edge) totalDistance += edge.weight;
            }
            
            document.getElementById('totalDistance').innerHTML = `${totalDistance.toLocaleString()} <small>km</small>`;
            document.getElementById('estTime').innerHTML = `${Math.ceil(totalDistance / 100)} <small>hrs</small>`;
            
            // Build path list
            const pathList = document.getElementById('pathList');
            pathList.innerHTML = path.map((loc, index) => {
                let dotClass = 'start';
                let detail = 'Starting Point';
                
                if (index === path.length - 1) {
                    dotClass = 'end';
                    detail = 'Destination Reached';
                } else if (index > 0) {
                    dotClass = 'middle';
                    // Calculate distance from previous
                    const prevLoc = path[index - 1];
                    const edge = edges.find(e => 
                        (e.start === prevLoc.id && e.end === loc.id) ||
                        (e.start === loc.id && e.end === prevLoc.id)
                    );
                    detail = edge ? `${edge.weight} km` : '';
                }
                
                return `
                    <div class="path-item">
                        <div class="path-dot ${dotClass}"></div>
                        <div class="path-info">
                            <div class="path-name">${loc.name}</div>
                            <div class="path-detail">${detail}</div>
                        </div>
                    </div>
                `;
            }).join('');
            
            addLog('success', `Found path: ${path.map(p => p.name).join(' → ')}`);
        });
}

function getLocationById(id) {
    return locations.find(loc => loc.id === id);
}

function isEdgeInPath(edge) {
    for (let i = 0; i < currentPath.length - 1; i++) {
        const curr = currentPath[i].id;
        const next = currentPath[i+1].id;
        if ((edge.start === curr && edge.end === next) || 
            (edge.start === next && edge.end === curr)) {
            return true;
        }
    }
    return false;
}

// ========================================
// Tour Tab (Linked List)
// ========================================
function initTourTab() {
    loadTourList();
    
    // Click tour card with image to set as background
    document.getElementById('tourList').addEventListener('click', (e) => {
        const card = e.target.closest('.tour-card-clickable');
        if (card && !e.target.closest('.btn-remove')) {
            const img = card.getAttribute('data-image');
            if (img) setTourBackground(img);
        }
    });
    
    // Position buttons
    document.querySelectorAll('.pos-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.pos-btn').forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            
            const indexInput = document.getElementById('indexInput');
            if (btn.dataset.position === 'index') {
                indexInput.style.display = 'block';
            } else {
                indexInput.style.display = 'none';
            }
        });
    });
    
    document.getElementById('addToTourBtn').addEventListener('click', addToTour);
    
    document.getElementById('tourImageInput').addEventListener('change', (e) => {
        const file = e.target.files[0];
        const preview = document.getElementById('imagePreview');
        if (file && file.type.startsWith('image/')) {
            const reader = new FileReader();
            reader.onload = () => {
                preview.innerHTML = `<img src="${reader.result}" alt="Preview">`;
            };
            reader.readAsDataURL(file);
        } else if (!file) {
            preview.innerHTML = '<span class="preview-placeholder">Chọn ảnh để upload</span>';
        }
    });
    
    document.getElementById('resetTourBtn').addEventListener('click', () => {
        if (!confirm('Are you sure you want to reset the entire tour?')) return;
        
        fetch('/api/tour')
            .then(res => res.json())
            .then(tour => {
                const promises = tour.map(loc => 
                    fetch(`/api/tour?id=${loc.id}`, { method: 'DELETE' })
                        .then(res => res.json())
                );
                return Promise.all(promises);
            })
            .then(() => {
                loadTourList();
                setTourBackground('');
                addLog('warning', 'Tour list has been reset');
            });
    });
    
    // Export button
    document.getElementById('exportBtn').addEventListener('click', exportTourJSON);
}

async function addToTour() {
    const locId = document.getElementById('tourLocationSelect').value;
    const priceInput = document.getElementById('tourPrice');
    const imageInput = document.getElementById('tourImageInput');
    
    if (!locId) {
        alert('Please select a location');
        return;
    }
    
    let imageUrl = '';
    if (imageInput && imageInput.files && imageInput.files[0]) {
        try {
            const formData = new FormData();
            formData.append('file', imageInput.files[0]);
            const uploadRes = await fetch('/api/upload', { method: 'POST', body: formData });
            const uploadData = await uploadRes.json();
            if (uploadData.url) imageUrl = uploadData.url;
            else if (uploadData.error) {
                alert('Upload ảnh thất bại: ' + uploadData.error);
                return;
            }
        } catch (err) {
            try {
                const base64 = await fileToBase64(imageInput.files[0]);
                const res = await fetch('/api/upload', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ image: base64, filename: imageInput.files[0].name })
                });
                const data = await res.json();
                if (data.url) imageUrl = data.url;
            } catch (e) {
                alert('Upload ảnh thất bại. Vui lòng thử lại.');
                return;
            }
        }
    }
    
    const price = priceInput ? (parseFloat(priceInput.value) || 0) : 0;
    const positionBtn = document.querySelector('.pos-btn.active');
    const position = positionBtn ? positionBtn.dataset.position : 'tail';
    const indexInput = document.getElementById('indexInput');
    
    let url = `/api/tour?id=${encodeURIComponent(locId)}&price=${encodeURIComponent(price)}&position=${encodeURIComponent(position)}`;
    if (position === 'index' && indexInput && indexInput.value !== '') {
        url += `&index=${encodeURIComponent(indexInput.value)}`;
    }
    if (imageUrl) url += `&imageUrl=${encodeURIComponent(imageUrl)}`;
    
    fetch(url, { method: 'POST' })
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                loadTourList();
                addLog('success', 'Added location to tour');
                if (imageInput) imageInput.value = '';
                const preview = document.getElementById('imagePreview');
                if (preview) preview.innerHTML = '<span class="preview-placeholder">Chọn ảnh để upload</span>';
            } else {
                alert(data.error || 'Failed to add location');
            }
        });
}

function fileToBase64(file) {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.onload = () => {
            let result = reader.result;
            if (typeof result === 'string' && result.startsWith('data:')) {
                result = result.split(',')[1];
            }
            resolve(result);
        };
        reader.onerror = reject;
        reader.readAsDataURL(file);
    });
}

function loadTourList() {
    fetch('/api/tour')
        .then(res => res.json())
        .then(tour => {
            const container = document.getElementById('tourList');
            const nodeCountEl = document.getElementById('nodeCount');
            const memoryUsedEl = document.getElementById('memoryUsed');
            
            nodeCountEl.textContent = tour.length;
            if (memoryUsedEl) memoryUsedEl.textContent = `${tour.length} Nodes Allocated`;
            
            if (tour.length === 0) {
                container.innerHTML = `
                    <div class="empty-state">
                        <div class="empty-icon">📍</div>
                        <p class="empty-title">No destinations in tour yet.</p>
                        <p class="empty-hint">Add locations from the sidebar to build your itinerary!</p>
                    </div>
                `;
                setTourBackground('');
                return;
            }
            
            // Build visual cards with arrows
            let html = '<div class="start-marker">START</div>';
            
            tour.forEach((loc, index) => {
                const isHead = index === 0;
                const isTail = index === tour.length - 1;
                const nextLoc = index < tour.length - 1 ? tour[index + 1].name : null;
                
                const priceStr = loc.price > 0 ? new Intl.NumberFormat('vi-VN').format(loc.price) + ' VNĐ' : 'Chưa nhập';
                const priceLevel = loc.price > 1000000 ? '$$$$' : 
                                  loc.price > 500000 ? '$$$' : 
                                  loc.price > 200000 ? '$$' : '$';
                const duration = getDuration();
                const imgStyle = loc.imageUrl ? `background-image: url('${loc.imageUrl}'); background-size: cover;` : '';
                const dataImage = loc.imageUrl ? ` data-image="${String(loc.imageUrl).replace(/"/g, '&quot;').replace(/</g, '&lt;')}"` : '';
                const cardClass = loc.imageUrl ? ' tour-card-clickable' : '';
                html += `
                    <div class="tour-arrow">→</div>
                    <div class="tour-card ${isHead ? 'head-node' : ''} ${isTail ? 'tail-node' : ''}${cardClass}"${dataImage}>
                        <div class="tour-card-map" style="${imgStyle}">
                            ${isHead ? '<div class="head-badge">HEAD</div>' : ''}
                            ${isTail && !isHead ? '<div class="tail-badge">TAIL</div>' : ''}
                        </div>
                        <div class="tour-card-content">
                            <h4 class="tour-card-title">${loc.name}</h4>
                            <div class="tour-card-meta">
                                <span>📅 ${duration} Days</span>
                                <span>💰 ${priceStr}</span>
                            </div>
                            <div class="tour-card-index">Index: ${index}</div>
                            ${nextLoc ? `<div class="tour-card-next"><span>Next:</span><span>${nextLoc}</span></div>` : ''}
                            <div class="tour-card-actions">
                                <button class="btn-remove" onclick="removeTourLocation('${loc.id}')">🗑️ Remove</button>
                            </div>
                        </div>
                    </div>
                `;
            });
            
            container.innerHTML = html;
        });
}

function getDuration() {
    const durationInput = document.getElementById('tourDuration');
    return durationInput ? durationInput.value : 3;
}

function removeTourLocation(id) {
    fetch(`/api/tour?id=${id}`, { method: 'DELETE' })
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                loadTourList();
                addLog('info', 'Removed location from tour');
            } else {
                alert(data.error || 'Failed to remove location');
            }
        });
}

function exportTourJSON() {
    fetch('/api/tour')
        .then(res => res.json())
        .then(tour => {
            const dataStr = JSON.stringify(tour, null, 2);
            const dataBlob = new Blob([dataStr], {type: 'application/json'});
            const url = URL.createObjectURL(dataBlob);
            
            const link = document.createElement('a');
            link.href = url;
            link.download = 'itinerary.json';
            link.click();
            
            URL.revokeObjectURL(url);
            addLog('success', 'Exported itinerary to JSON');
        });
}

// ========================================
// Customers Tab (BST)
// ========================================
function initCustomersTab() {
    loadCustomers();
    
    // Modal controls
    const modal = document.getElementById('addCustomerModal');
    const addBtn = document.getElementById('addCustomerBtn');
    const closeBtn = document.getElementById('closeModalBtn');
    const cancelBtn = document.getElementById('cancelModalBtn');
    const confirmBtn = document.getElementById('confirmAddBtn');
    
    addBtn.addEventListener('click', () => modal.classList.remove('hidden'));
    closeBtn.addEventListener('click', () => modal.classList.add('hidden'));
    cancelBtn.addEventListener('click', () => modal.classList.add('hidden'));
    
    confirmBtn.addEventListener('click', addCustomer);
    
    // Search
    document.getElementById('searchCustomerId').addEventListener('keyup', (e) => {
        if (e.key === 'Enter') {
            searchCustomer();
        }
    });
}

function loadCustomers() {
    fetch('/api/customers?_t=' + Date.now())
        .then(res => res.json())
        .then(data => {
            document.getElementById('totalCustomers').textContent = data.count || 0;
            document.getElementById('treeHeight').textContent = Math.ceil(Math.log2((data.count || 0) + 1)) || 0;
            document.getElementById('treeDepth').textContent = Math.ceil(Math.log2((data.count || 0) + 1)) || 0;
            
            const customers = data.customers || [];
            renderCustomerTable(customers);
            drawBST(data.tree || []);
        })
        .catch(err => {
            console.error('Failed to load customers:', err);
            renderCustomerTable([]);
            drawBST([]);
        });
}

function renderCustomerTable(customers) {
    const tableBody = document.getElementById('customerTableBody');
    
    if (!customers || customers.length === 0) {
        tableBody.innerHTML = '<tr><td colspan="6" class="empty-state">No customers yet. Click "Add Customer" to add one.</td></tr>';
        return;
    }
    
    tableBody.innerHTML = customers.map(cust => {
        const displayId = cust.id.startsWith('CUS') ? cust.id.replace('CUS', '') : cust.id;
        const location = cust.location || (cust.email ? cust.email.split('@')[1] || '-' : '-');
        const bookings = cust.bookings != null ? cust.bookings : '-';
        const status = cust.status || 'active';
        return `
        <tr>
            <td class="customer-id">#${displayId}</td>
            <td>${cust.name}</td>
            <td>${location}</td>
            <td>${bookings}</td>
            <td>
                <span class="status-badge status-${status}">
                    ${status.charAt(0).toUpperCase() + status.slice(1)}
                </span>
            </td>
            <td>
                <button class="btn-action" onclick="searchCustomerById('${cust.id}')" title="Tìm kiếm">✏️</button>
                <button class="btn-action btn-delete" onclick="deleteCustomer('${cust.id}')" title="Xóa">🗑️</button>
            </td>
        </tr>
    `}).join('');
}

function drawBST(treeData) {
    const canvas = document.getElementById('bstCanvas');
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    
    if (!treeData || treeData.length === 0) {
        ctx.fillStyle = '#9ca3af';
        ctx.font = '14px Inter';
        ctx.textAlign = 'center';
        ctx.fillText('Tree is empty. Add customers to see the BST visualization.', canvas.width / 2, canvas.height / 2);
        return;
    }
    
    // Find root (node not referenced as left/right of any other)
    const childIds = new Set();
    treeData.forEach(n => {
        if (n.left) childIds.add(n.left);
        if (n.right) childIds.add(n.right);
    });
    const rootNode = treeData.find(n => !childIds.has(n.id));
    if (!rootNode) return;
    
    // Build layout with positions
    const nodeMap = {};
    treeData.forEach(n => { nodeMap[n.id] = { ...n, x: 0, y: 0, children: [n.left, n.right].filter(Boolean) }; });
    
    const canvasW = canvas.width;
    const canvasH = canvas.height;
    const yStep = 80;
    
    function layout(nodeId, left, right, depth) {
        if (!nodeId) return;
        const node = nodeMap[nodeId];
        if (!node) return;
        const mid = (left + right) / 2;
        node.x = mid;
        node.y = 40 + depth * yStep;
        const half = (right - left) / 4;
        layout(node.left, left, mid, depth + 1);
        layout(node.right, mid, right, depth + 1);
    }
    layout(rootNode.id, 50, canvasW - 50, 0);
    
    const nodes = Object.values(nodeMap);
    
    // Draw connections
    ctx.strokeStyle = '#e5e7eb';
    ctx.lineWidth = 2;
    nodes.forEach(node => {
        node.children.forEach(childId => {
            const child = nodeMap[childId];
            if (child) {
                ctx.beginPath();
                ctx.moveTo(node.x, node.y + 25);
                ctx.lineTo(child.x, child.y - 25);
                ctx.stroke();
            }
        });
    });
    
    // Draw nodes
    nodes.forEach(node => {
        const width = 140;
        const height = 50;
        const x = node.x - width/2;
        const y = node.y - height/2;
        
        ctx.fillStyle = '#f0f9ff';
        ctx.strokeStyle = '#3b82f6';
        ctx.lineWidth = 2;
        ctx.beginPath();
        ctx.roundRect(x, y, width, height, 8);
        ctx.fill();
        ctx.stroke();
        
        ctx.beginPath();
        ctx.arc(node.x + width/2 - 10, node.y - height/2 + 10, 4, 0, Math.PI * 2);
        ctx.fillStyle = '#22c55e';
        ctx.fill();
        
        const displayId = node.id.startsWith('CUS') ? node.id.replace('CUS', '') : node.id;
        ctx.fillStyle = '#3b82f6';
        ctx.font = '11px Inter';
        ctx.textAlign = 'left';
        ctx.textBaseline = 'middle';
        ctx.fillText(`ID: ${displayId}`, x + 10, y + 15);
        
        ctx.fillStyle = '#1f2937';
        ctx.font = 'bold 13px Inter';
        const shortName = node.name.length > 14 ? node.name.substring(0, 12) + '...' : node.name;
        ctx.fillText(shortName, x + 10, y + 35);
    });
}

function addCustomer() {
    let id = document.getElementById('custId').value.trim();
    const name = document.getElementById('custName').value.trim();
    const phone = document.getElementById('custPhone').value.trim();
    const email = document.getElementById('custEmail').value.trim();
    
    if (!id || !name || !phone || !email) {
        alert('Vui lòng điền đầy đủ các trường');
        return;
    }
    
    // Normalize ID: "004" or "4" -> "CUS004"
    if (!id.toUpperCase().startsWith('CUS')) {
        id = 'CUS' + id.padStart(3, '0');
    }
    
    fetch(`/api/customers?id=${encodeURIComponent(id)}&name=${encodeURIComponent(name)}&phone=${encodeURIComponent(phone)}&email=${encodeURIComponent(email)}`, 
          { method: 'POST' })
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                // Clear form
                document.getElementById('custId').value = '';
                document.getElementById('custName').value = '';
                document.getElementById('custPhone').value = '';
                document.getElementById('custEmail').value = '';
                
                // Close modal
                document.getElementById('addCustomerModal').classList.add('hidden');
                
                loadCustomers();
                addLog('success', `Added customer: ${name}`);
            } else {
                addLog('error', data.error || 'Failed to add customer');
                alert(data.error || 'Không thể thêm khách hàng');
            }
        })
        .catch(err => {
            addLog('error', 'Lỗi kết nối khi thêm khách hàng');
            alert('Lỗi kết nối. Vui lòng thử lại.');
        });
}

function searchCustomer() {
    let id = document.getElementById('searchCustomerId').value.trim();
    if (!id) return;
    
    // Chuẩn hóa ID: "001" hoặc "1" → "CUS001"
    if (!id.toUpperCase().startsWith('CUS')) {
        id = 'CUS' + id.padStart(3, '0');
    }
    
    searchCustomerById(id);
}

function searchCustomerById(id) {
    fetch(`/api/customers?id=${encodeURIComponent(id)}`)
        .then(res => res.json())
        .then(data => {
            if (data.error) {
                addLog('error', `Không tìm thấy: ${id}`);
                alert('Không tìm thấy khách hàng');
            } else {
                addLog('info', `Tìm thấy: ${data.name}`);
                alert(`Tìm thấy!\n\nID: ${data.id}\nTên: ${data.name}\nĐiện thoại: ${data.phone}\nEmail: ${data.email}`);
            }
        })
        .catch(() => {
            addLog('error', 'Lỗi kết nối khi tìm kiếm');
            alert('Lỗi kết nối. Vui lòng thử lại.');
        });
}

function deleteCustomer(id) {
    if (!confirm('Bạn có chắc muốn xóa khách hàng ' + id + '?')) return;
    fetch(`/api/customers?id=${encodeURIComponent(id)}`, { method: 'DELETE' })
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                loadCustomers();
                addLog('success', `Deleted customer: ${id}`);
            } else {
                alert(data.error || 'Không thể xóa khách hàng');
            }
        })
        .catch(err => {
            addLog('error', 'Failed to delete customer');
            alert('Không thể xóa khách hàng');
        });
}

// ========================================
// Activity Logs
// ========================================
function addLog(type, message) {
    const log = {
        type,
        message,
        time: new Date().toLocaleTimeString('en-US', { hour12: false })
    };
    
    activityLogs.unshift(log);
    if (activityLogs.length > 10) activityLogs.pop();
    
    renderLogs();
}

function renderLogs() {
    const container = document.getElementById('logsContainer');
    if (!container) return;
    
    container.innerHTML = activityLogs.slice(0, 5).map(log => {
        let icon = '📋';
        let iconClass = 'search';
        
        if (log.type === 'success') {
            icon = '✅';
            iconClass = 'add';
        } else if (log.type === 'error') {
            icon = '❌';
            iconClass = 'remove';
        } else if (log.type === 'warning') {
            icon = '⚠️';
            iconClass = 'remove';
        }
        
        return `
            <div class="log-item">
                <span class="log-icon ${iconClass}">${icon}</span>
                <span class="log-text">${log.message}</span>
                <span class="log-time">${log.time}</span>
            </div>
        `;
    }).join('');
}

function setTourBackground(imageUrl) {
    const container = document.getElementById('tourList');
    if (!container) return;
    if (imageUrl) {
        container.style.backgroundImage = `url('${imageUrl.replace(/'/g, "\\'")}')`;
        container.classList.add('has-bg-image');
    } else {
        container.style.backgroundImage = '';
        container.classList.remove('has-bg-image');
    }
}

// Make functions globally accessible
window.removeTourLocation = removeTourLocation;
window.searchCustomerById = searchCustomerById;
window.setTourBackground = setTourBackground;
