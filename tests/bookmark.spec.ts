import { test, expect } from '@playwright/test';
import { generateUsername, generatePostTitle, signup, login, createPost } from './utils';

test.describe('북마크 기능 테스트', () => {
  let username: string;
  let postTitle: string;

  test.beforeEach(async ({ page }) => {
    username = generateUsername();
    postTitle = generatePostTitle();
    
    await signup(page, username);
    await login(page, username);
    await createPost(page, postTitle, '북마크 테스트용 게시글');
  });

  test('북마크 추가', async ({ page }) => {
    // 게시글 상세 페이지로 이동
    await page.click('.post-card a').first();
    
    // 북마크 버튼 확인
    await expect(page.locator('.btn-bookmark')).toBeVisible();
    
    // 북마크 추가
    await page.click('.btn-bookmark');
    
    // 아이콘 변경 확인 (채워진 북마크)
    await page.waitForTimeout(500);
    await expect(page.locator('.btn-bookmark i')).toHaveClass(/bi-bookmark-fill/);
  });

  test('북마크 목록 페이지 접근', async ({ page }) => {
    await page.goto('/bookmarks');
    
    // 페이지 제목 확인
    await expect(page.locator('h1')).toContainText('My Bookmarks');
    
    // 빈 상태 메시지 (아직 북마크 없음)
    await expect(page.locator('.empty-state')).toContainText('저장한 북마크가 없습니다');
  });

  test('북마크 목록 표시', async ({ page }) => {
    // 게시글에 북마크 추가
    await page.click('.post-card a').first();
    await page.click('.btn-bookmark');
    
    // 북마크 목록으로 이동
    await page.goto('/bookmarks');
    
    // 북마크된 게시글 확인
    await expect(page.locator('.post-card')).toContainText(postTitle);
    await expect(page.locator('.author')).toContainText(username);
  });

  test('북마크 목록에서 게시글로 이동', async ({ page }) => {
    // 북마크 추가
    await page.click('.post-card a').first();
    await page.click('.btn-bookmark');
    
    // 북마크 목록
    await page.goto('/bookmarks');
    
    // 보기 버튼 클릭
    await page.click('a:has-text("보기")').first();
    
    // 게시글 상세 페이지로 이동 확인
    await expect(page).toHaveURL(/\/board\/\d+/);
    await expect(page.locator('.post-title')).toContainText(postTitle);
  });

  test('북마크 삭제', async ({ page }) => {
    // 북마크 추가
    await page.click('.post-card a').first();
    await page.click('.btn-bookmark');
    
    // 북마크 목록
    await page.goto('/bookmarks');
    
    // 삭제 확인 다이얼로그
    page.once('dialog', dialog => {
      expect(dialog.message()).toContain('삭제하시겠습니까');
      dialog.accept();
    });
    
    await page.click('button:has-text("삭제")').first();
    
    page.once('dialog', dialog => {
      expect(dialog.message()).toContain('삭제되었습니다');
      dialog.accept();
    });
    
    await page.waitForTimeout(1000);
    await page.reload();
    
    // 빈 상태 확인
    await expect(page.locator('.empty-state')).toBeVisible();
  });

  test('여러 게시글 북마크', async ({ page }) => {
    // 3개 게시글 작성 및 북마크
    for (let i = 1; i <= 3; i++) {
      await createPost(page, `북마크 ${i}`, `내용 ${i}`);
    }
    
    await page.goto('/board');
    
    // 각 게시글에 북마크 추가
    const postLinks = await page.locator('.post-card a').all();
    for (let i = 0; i < Math.min(3, postLinks.length); i++) {
      await page.click('.post-card a').nth(i);
      await page.click('.btn-bookmark');
      await page.goto('/board');
    }
    
    // 북마크 목록 확인
    await page.goto('/bookmarks');
    
    const bookmarkCards = await page.locator('.post-card').count();
    expect(bookmarkCards).toBe(3);
  });

  test('네비게이션 메뉴에 북마크 링크 표시', async ({ page }) => {
    await page.goto('/board');
    
    // 로그인한 사용자는 북마크 링크 보임
    await expect(page.locator('a:has-text("북마크")')).toBeVisible();
    
    // 클릭 시 이동
    await page.click('a:has-text("북마크")');
    await expect(page).toHaveURL('/bookmarks');
  });

  test('로그인하지 않은 사용자 - 북마크 버튼 숨김', async ({ page }) => {
    // 로그아웃
    await page.goto('/board');
    await page.click('button[type="submit"]:has-text("로그아웃")');
    
    // 게시글로 이동
    await page.click('.post-card a').first();
    
    // 북마크 버튼 없음
    await expect(page.locator('.btn-bookmark')).not.toBeVisible();
  });
});
