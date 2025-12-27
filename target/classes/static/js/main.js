/**
 * CMC Board - Main JavaScript
 * Modern interactions and API integrations
 */

// ===================================
// 1. Global Utilities
// ===================================

/**
 * API 요청 헬퍼 함수
 */
const api = {
    baseUrl: window.location.origin,
    
    async get(endpoint) {
        const response = await fetch(`${this.baseUrl}${endpoint}`);
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        return response.json();
    },
    
    async post(endpoint, data) {
        const response = await fetch(`${this.baseUrl}${endpoint}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data)
        });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        return response;
    },
    
    async put(endpoint, data) {
        const response = await fetch(`${this.baseUrl}${endpoint}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data)
        });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        return response;
    },
    
    async delete(endpoint) {
        const response = await fetch(`${this.baseUrl}${endpoint}`, {
            method: 'DELETE'
        });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        return response;
    }
};

/**
 * 토스트 알림 표시
 */
function showToast(message, type = 'info') {
    const toast = document.createElement('div');
    toast.className = `toast-notification toast-${type}`;
    toast.innerHTML = `
        <i class="bi bi-${getToastIcon(type)} me-2"></i>
        <span>${message}</span>
    `;
    
    document.body.appendChild(toast);
    
    setTimeout(() => toast.classList.add('show'), 100);
    
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

function getToastIcon(type) {
    const icons = {
        success: 'check-circle-fill',
        error: 'exclamation-circle-fill',
        warning: 'exclamation-triangle-fill',
        info: 'info-circle-fill'
    };
    return icons[type] || icons.info;
}

/**
 * 로딩 스피너 표시/숨김
 */
function showLoading(button) {
    button.disabled = true;
    button.dataset.originalText = button.innerHTML;
    button.innerHTML = '<span class="spinner"></span> 처리 중...';
}

function hideLoading(button) {
    button.disabled = false;
    button.innerHTML = button.dataset.originalText;
}

/**
 * 날짜 포맷팅
 */
function formatDate(dateString) {
    const date = new Date(dateString);
    const now = new Date();
    const diff = now - date;
    
    const seconds = Math.floor(diff / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);
    
    if (seconds < 60) return '방금 전';
    if (minutes < 60) return `${minutes}분 전`;
    if (hours < 24) return `${hours}시간 전`;
    if (days < 7) return `${days}일 전`;
    
    return date.toLocaleDateString('ko-KR');
}

// ===================================
// 2. Scroll Animations
// ===================================

const observerOptions = {
    threshold: 0.1,
    rootMargin: '0px 0px -50px 0px'
};

const observer = new IntersectionObserver((entries) => {
    entries.forEach((entry, index) => {
        if (entry.isIntersecting) {
            setTimeout(() => {
                entry.target.classList.add('visible');
            }, index * 100);
            observer.unobserve(entry.target);
        }
    });
}, observerOptions);

// 페이지 로드 시 애니메이션 적용
document.addEventListener('DOMContentLoaded', () => {
    const animatedElements = document.querySelectorAll('.post-card, .animate-on-scroll');
    animatedElements.forEach(el => observer.observe(el));
});

// ===================================
// 3. Smooth Scrolling
// ===================================

document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        e.preventDefault();
        const target = document.querySelector(this.getAttribute('href'));
        if (target) {
            target.scrollIntoView({
                behavior: 'smooth',
                block: 'start'
            });
        }
    });
});

// ===================================
// 4. Search Functionality
// ===================================

let searchTimeout;
const searchInput = document.querySelector('input[name="keyword"]');

if (searchInput) {
    searchInput.addEventListener('input', (e) => {
        clearTimeout(searchTimeout);
        searchTimeout = setTimeout(() => {
            const keyword = e.target.value.trim();
            if (keyword.length >= 2) {
                console.log('Searching for:', keyword);
                // 실시간 검색 결과 표시 (선택사항)
            }
        }, 500);
    });
}

// ===================================
// 5. Form Validation
// ===================================

/**
 * 폼 유효성 검사 초기화
 */
function initFormValidation() {
    const forms = document.querySelectorAll('form[data-validate]');
    
    forms.forEach(form => {
        form.addEventListener('submit', (e) => {
            if (!form.checkValidity()) {
                e.preventDefault();
                e.stopPropagation();
            }
            form.classList.add('was-validated');
        });
    });
}

// ===================================
// 6. Image Upload Preview
// ===================================

function setupImageUpload() {
    const imageInputs = document.querySelectorAll('input[type="file"][accept="image/*"]');
    
    imageInputs.forEach(input => {
        input.addEventListener('change', (e) => {
            const file = e.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = (event) => {
                    const preview = input.nextElementSibling;
                    if (preview && preview.classList.contains('image-preview')) {
                        preview.src = event.target.result;
                        preview.style.display = 'block';
                    }
                };
                reader.readAsDataURL(file);
            }
        });
    });
}

// ===================================
// 7. Auto-save Draft (선택사항)
// ===================================

function setupAutoSave() {
    const editor = document.querySelector('.content-editor');
    if (!editor) return;
    
    let autoSaveTimeout;
    
    editor.addEventListener('input', () => {
        clearTimeout(autoSaveTimeout);
        autoSaveTimeout = setTimeout(() => {
            const content = editor.value;
            localStorage.setItem('draft', content);
            showToast('임시저장 완료', 'success');
        }, 3000);
    });
    
    // 페이지 로드 시 임시저장 복구
    const draft = localStorage.getItem('draft');
    if (draft && editor.value === '') {
        editor.value = draft;
        showToast('임시저장된 내용을 불러왔습니다', 'info');
    }
}

// ===================================
// 8. Keyboard Shortcuts
// ===================================

document.addEventListener('keydown', (e) => {
    // Ctrl/Cmd + K: 검색 포커스
    if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
        e.preventDefault();
        const searchInput = document.querySelector('input[name="keyword"]');
        if (searchInput) searchInput.focus();
    }
    
    // Ctrl/Cmd + Enter: 폼 제출
    if ((e.ctrlKey || e.metaKey) && e.key === 'Enter') {
        const activeForm = document.activeElement.closest('form');
        if (activeForm) {
            activeForm.requestSubmit();
        }
    }
    
    // ESC: 모달 닫기, 폼 취소
    if (e.key === 'Escape') {
        const modal = document.querySelector('.modal.show');
        if (modal) {
            bootstrap.Modal.getInstance(modal).hide();
        }
    }
});

// ===================================
// 9. Navbar Scroll Effect
// ===================================

let lastScrollTop = 0;
const navbar = document.querySelector('.navbar');

window.addEventListener('scroll', () => {
    const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
    
    if (scrollTop > lastScrollTop && scrollTop > 100) {
        // Scrolling down
        navbar.style.transform = 'translateY(-100%)';
    } else {
        // Scrolling up
        navbar.style.transform = 'translateY(0)';
    }
    
    // Add shadow on scroll
    if (scrollTop > 50) {
        navbar.classList.add('scrolled');
    } else {
        navbar.classList.remove('scrolled');
    }
    
    lastScrollTop = scrollTop;
});

// ===================================
// 10. Copy to Clipboard
// ===================================

async function copyToClipboard(text) {
    try {
        await navigator.clipboard.writeText(text);
        showToast('클립보드에 복사되었습니다', 'success');
    } catch (err) {
        console.error('Failed to copy:', err);
        showToast('복사에 실패했습니다', 'error');
    }
}

// ===================================
// 11. Share Functionality
// ===================================

async function sharePost(title, url) {
    if (navigator.share) {
        try {
            await navigator.share({
                title: title,
                url: url
            });
            showToast('공유되었습니다', 'success');
        } catch (err) {
            console.log('Share cancelled:', err);
        }
    } else {
        // Fallback: 링크 복사
        copyToClipboard(url);
    }
}

// ===================================
// 12. Dark Mode Toggle (선택사항)
// ===================================

function initDarkMode() {
    const darkModeToggle = document.querySelector('.dark-mode-toggle');
    if (!darkModeToggle) return;
    
    // 저장된 테마 불러오기
    const savedTheme = localStorage.getItem('theme') || 'light';
    document.documentElement.setAttribute('data-theme', savedTheme);
    
    darkModeToggle.addEventListener('click', () => {
        const currentTheme = document.documentElement.getAttribute('data-theme');
        const newTheme = currentTheme === 'light' ? 'dark' : 'light';
        
        document.documentElement.setAttribute('data-theme', newTheme);
        localStorage.setItem('theme', newTheme);
        
        showToast(`${newTheme === 'dark' ? '다크' : '라이트'} 모드로 전환되었습니다`, 'info');
    });
}

// ===================================
// 13. Lazy Loading Images
// ===================================

function setupLazyLoading() {
    const images = document.querySelectorAll('img[data-src]');
    
    const imageObserver = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                const img = entry.target;
                img.src = img.dataset.src;
                img.removeAttribute('data-src');
                imageObserver.unobserve(img);
            }
        });
    });
    
    images.forEach(img => imageObserver.observe(img));
}

// ===================================
// 14. Initialize on Page Load
// ===================================

document.addEventListener('DOMContentLoaded', () => {
    console.log('CMC Board initialized');
    
    // 각종 기능 초기화
    initFormValidation();
    setupImageUpload();
    setupAutoSave();
    initDarkMode();
    setupLazyLoading();
    
    // 툴팁 초기화 (Bootstrap)
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
});

// ===================================
// 15. Export for Global Use
// ===================================

window.BoardApp = {
    api,
    showToast,
    showLoading,
    hideLoading,
    formatDate,
    copyToClipboard,
    sharePost
};