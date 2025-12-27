import { Page } from '@playwright/test';

/**
 * 테스트 유틸리티 함수 모음
 */

// 랜덤 사용자명 생성 (4~20자 제한)
export function generateUsername(): string {
  const timestamp = Date.now().toString().slice(-6); // 마지막 6자리만
  const random = Math.floor(Math.random() * 100); // 0~99
  return `test${timestamp}`; // 예: test78901234 (12자)
}

// 랜덤 게시글 제목 생성
export function generatePostTitle(): string {
  return `테스트 게시글 ${Date.now()}`;
}

// 회원가입
export async function signup(page: Page, username: string, password: string = 'test1234') {
  await page.goto('/auth/signup');
  
  // 페이지 완전히 로딩될 때까지 대기
  await page.waitForLoadState('networkidle');
  await page.waitForSelector('#username', { state: 'visible', timeout: 5000 });
  
  // 폼 입력
  await page.fill('#username', username);
  await page.fill('#password', password);
  await page.fill('#passwordConfirm', password);
  
  // 이용약관 체크박스 대기 및 체크
  await page.waitForSelector('#agreeTerms', { state: 'visible' });
  await page.check('#agreeTerms', { force: true });
  
  // 체크 확인
  await page.waitForTimeout(300);
  const isChecked = await page.isChecked('#agreeTerms');
  if (!isChecked) {
    console.error('❌ 이용약관 체크 실패!');
    await page.screenshot({ path: `test-results/checkbox-fail-${Date.now()}.png` });
    throw new Error('Failed to check terms');
  }
  
  console.log('✅ 이용약관 동의 체크 완료');
  
  // 제출 버튼 클릭 전에 네비게이션과 dialog 대기 준비
  const [response] = await Promise.all([
    page.waitForResponse(
      response => response.url().includes('/api/auth/signup'),
      { timeout: 10000 }
    ).catch(() => null),
    page.waitForEvent('dialog', { timeout: 5000 }).then(dialog => {
      console.log('Dialog:', dialog.message());
      return dialog.accept();
    }).catch(() => console.log('No dialog appeared')),
    page.click('button[type="submit"]')
  ]);
  
  if (response) {
    console.log('API 응답:', response.status());
  }
  
  // 페이지 이동 대기 (유연한 타임아웃)
  try {
    await page.waitForURL('/auth/login', { timeout: 10000 });
    console.log('✅ 회원가입 성공, 로그인 페이지로 이동');
  } catch (error) {
    console.error('❌ 회원가입 실패 또는 리디렉션 실패');
    console.log('Current URL:', page.url());
    
    // 페이지 내용 확인
    const bodyText = await page.textContent('body');
    console.log('Page content preview:', bodyText?.substring(0, 200));
    
    await page.screenshot({ path: `test-results/signup-error-${Date.now()}.png`, fullPage: true });
    throw error;
  }
}

// 로그인
export async function login(page: Page, username: string, password: string = 'test1234') {
  await page.goto('/auth/login');
  
  // 페이지 로딩 대기
  await page.waitForLoadState('networkidle');
  await page.waitForSelector('#username', { state: 'visible', timeout: 5000 });
  
  await page.fill('#username', username);
  await page.fill('#password', password);
  
  // 제출 및 리디렉션 대기
  await Promise.all([
    page.waitForURL('/board', { timeout: 10000 }),
    page.click('button[type="submit"]')
  ]);
  
  console.log('✅ 로그인 성공');
}

// 로그아웃
export async function logout(page: Page) {
  await page.click('button[type="submit"]:has-text("로그아웃")');
  await page.waitForURL('/board', { timeout: 5000 });
  console.log('✅ 로그아웃 성공');
}

// 게시글 작성
export async function createPost(
  page: Page, 
  title: string, 
  content: string, 
  categoryIds: number[] = [1]
) {
  await page.goto('/board/write');
  
  // 페이지 로딩 대기
  await page.waitForLoadState('networkidle');
  await page.waitForSelector('#title', { state: 'visible', timeout: 5000 });
  
  // 카테고리 선택
  for (const id of categoryIds) {
    const checkbox = page.locator(`input[value="${id}"]`);
    if (await checkbox.isVisible()) {
      await checkbox.check();
    }
  }
  
  await page.fill('#title', title);
  await page.fill('#content', content);
  
  // 제출 및 dialog/리디렉션 대기
  await Promise.all([
    page.waitForEvent('dialog').then(dialog => {
      console.log('게시글 작성 dialog:', dialog.message());
      return dialog.accept();
    }).catch(() => console.log('No dialog')),
    page.waitForURL('/board', { timeout: 10000 }).catch(() => {}),
    page.click('button[type="submit"]')
  ]);
  
  console.log('✅ 게시글 작성 완료');
}

// 댓글 작성
export async function createComment(page: Page, content: string) {
  await page.fill('#commentContent', content);
  
  await Promise.all([
    page.waitForEvent('dialog').then(dialog => {
      console.log('댓글 작성 dialog:', dialog.message());
      return dialog.accept();
    }).catch(() => {}),
    page.click('button[type="submit"]:has-text("댓글 작성")')
  ]);
  
  await page.waitForTimeout(1500);
  console.log('✅ 댓글 작성 완료');
}

// 카테고리 추가
export async function createCategory(page: Page, name: string) {
  await page.goto('/admin/categories');
  await page.waitForLoadState('networkidle');
  await page.waitForSelector('#categoryName', { state: 'visible', timeout: 5000 });
  await page.fill('#categoryName', name);
  
  await Promise.all([
    page.waitForEvent('dialog').then(dialog => {
      console.log('카테고리 추가 dialog:', dialog.message());
      return dialog.accept();
    }).catch(() => {}),
    page.click('button[type="submit"]:has-text("추가")')
  ]);
  
  await page.waitForTimeout(1500);
  console.log('✅ 카테고리 추가 완료');
}

// 스크린샷 저장 (디버깅용)
export async function takeScreenshot(page: Page, name: string) {
  await page.screenshot({ 
    path: `test-results/${name}-${Date.now()}.png`, 
    fullPage: true 
  });
  console.log(`📸 스크린샷 저장: ${name}`);
}

// 페이지 에러 로그 수집
export async function setupErrorLogging(page: Page) {
  page.on('console', msg => {
    const type = msg.type();
    if (type === 'error' || type === 'warning') {
      console.log(`[Browser ${type}]:`, msg.text());
    }
  });
  
  page.on('pageerror', error => {
    console.error('[Page error]:', error.message);
  });
  
  page.on('requestfailed', request => {
    console.error('[Request failed]:', request.url(), request.failure()?.errorText);
  });
  
  page.on('response', response => {
    if (response.status() >= 400) {
      console.error(`[HTTP ${response.status()}]:`, response.url());
    }
  });
}