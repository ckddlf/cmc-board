import { Page } from '@playwright/test';

/**
 * 테스트 유틸리티 함수 모음
 */

// 랜덤 사용자명 생성
export function generateUsername(): string {
  return `testuser${Date.now()}${Math.floor(Math.random() * 1000)}`;
  //return "user1";
}

// 랜덤 게시글 제목 생성
export function generatePostTitle(): string {
  return `테스트 게시글 ${Date.now()}`;
}

// 회원가입
export async function signup(page: Page, username: string, password: string = 'test1234') {
  await page.goto('/auth/signup');
  await page.fill('#username', username);
  await page.fill('#password', password);
  await page.fill('#passwordConfirm', password);
  await page.check('#agreeTerms');
  await page.click('button[type="submit"]');
  
  // 알림 확인 및 닫기
  page.once('dialog', dialog => dialog.accept());
  await page.waitForURL('/auth/login');
}

// 로그인
export async function login(page: Page, username: string, password: string = 'test1234') {
  await page.goto('/auth/login');
  await page.fill('#username', username);
  await page.fill('#password', password);
  await page.click('button[type="submit"]');
  await page.waitForURL('/board');
}

// 로그아웃
export async function logout(page: Page) {
  await page.click('button[type="submit"]:has-text("로그아웃")');
  await page.waitForURL('/board');
}

// 게시글 작성
export async function createPost(
  page: Page, 
  title: string, 
  content: string, 
  categoryIds: number[] = [1]
) {
  await page.goto('/board/write');
  
  // 카테고리 선택
  for (const id of categoryIds) {
    await page.check(`input[value="${id}"]`);
  }
  
  await page.fill('#title', title);
  await page.fill('#content', content);
  await page.click('button[type="submit"]');
  
  // 알림 확인 및 닫기
  page.once('dialog', dialog => dialog.accept());
  await page.waitForURL('/board');
}

// 댓글 작성
export async function createComment(page: Page, content: string) {
  await page.fill('#commentContent', content);
  await page.click('button[type="submit"]:has-text("댓글 작성")');
  
  page.once('dialog', dialog => dialog.accept());
  await page.waitForTimeout(1000);
}

// 카테고리 추가
export async function createCategory(page: Page, name: string) {
  await page.goto('/admin/categories');
  await page.fill('#categoryName', name);
  await page.click('button[type="submit"]:has-text("추가")');
  
  page.once('dialog', dialog => dialog.accept());
  await page.waitForTimeout(1000);
}

// 스크린샷 저장
export async function takeScreenshot(page: Page, name: string) {
  await page.screenshot({ 
    path: `screenshots/${name}.png`, 
    fullPage: true 
  });
}
