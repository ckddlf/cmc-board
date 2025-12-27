import { test, expect } from '@playwright/test';
import { generateUsername, generatePostTitle, signup, login, createPost, createComment } from './utils';

test.describe('댓글 기능 테스트', () => {
  let username: string;
  let postTitle: string;

  test.beforeEach(async ({ page }) => {
    username = generateUsername();
    postTitle = generatePostTitle();
    
    await signup(page, username);
    await login(page, username);
    await createPost(page, postTitle, '댓글 테스트용 게시글');
    
    // 게시글 상세 페이지로 이동
    await page.click('.post-card a').first();
  });

  test('댓글 작성', async ({ page }) => {
    // 댓글 폼 확인
    await expect(page.locator('#commentForm')).toBeVisible();
    
    // 댓글 작성
    const commentContent = '테스트 댓글입니다.';
    await page.fill('#commentContent', commentContent);
    
    page.once('dialog', dialog => {
      expect(dialog.message()).toContain('작성되었습니다');
      dialog.accept();
    });
    
    await page.click('button:has-text("댓글 작성")');
    
    // 페이지 새로고침 대기
    await page.waitForTimeout(1000);
    await page.reload();
    
    // 작성된 댓글 확인
    await expect(page.locator('.comment-body')).toContainText(commentContent);
    await expect(page.locator('.comment-author')).toContainText(username);
  });

  test('답글 작성', async ({ page }) => {
    // 먼저 댓글 작성
    await createComment(page, '원본 댓글');
    await page.reload();
    
    // 답글 버튼 클릭
    await page.click('button:has-text("답글")').first();
    
    // 답글 폼 확인
    await expect(page.locator('.reply-form').first()).toBeVisible();
    
    // 답글 작성
    const replyContent = '이것은 답글입니다.';
    await page.fill('.reply-form textarea', replyContent);
    
    page.once('dialog', dialog => {
      expect(dialog.message()).toContain('작성되었습니다');
      dialog.accept();
    });
    
    await page.click('.reply-form button[type="submit"]');
    
    // 페이지 새로고침 대기
    await page.waitForTimeout(1000);
    await page.reload();
    
    // 답글 확인
    await expect(page.locator('.reply .comment-body')).toContainText(replyContent);
  });

  test('답글 폼 취소', async ({ page }) => {
    await createComment(page, '댓글');
    await page.reload();
    
    // 답글 버튼 클릭
    await page.click('button:has-text("답글")').first();
    await expect(page.locator('.reply-form').first()).toBeVisible();
    
    // 취소 버튼 클릭
    await page.click('button:has-text("취소")').first();
    
    // 답글 폼 숨김 확인
    await expect(page.locator('.reply-form').first()).toBeHidden();
  });

  test('댓글 삭제', async ({ page }) => {
    const commentContent = '삭제할 댓글';
    await createComment(page, commentContent);
    await page.reload();
    
    // 삭제 버튼 클릭
    page.once('dialog', dialog => {
      expect(dialog.message()).toContain('삭제하시겠습니까');
      dialog.accept();
    });
    
    await page.click('.comment-actions button:has-text("삭제")').first();
    
    page.once('dialog', dialog => {
      expect(dialog.message()).toContain('삭제되었습니다');
      dialog.accept();
    });
    
    await page.waitForTimeout(1000);
    await page.reload();
    
    // 삭제된 댓글이 없는지 확인
    const comments = await page.locator('.comment-body').allTextContents();
    expect(comments).not.toContain(commentContent);
  });

  test('댓글 카운트 확인', async ({ page }) => {
    // 초기 카운트
    await expect(page.locator('.comments-title .badge')).toContainText('0');
    
    // 댓글 3개 작성
    for (let i = 1; i <= 3; i++) {
      await createComment(page, `댓글 ${i}`);
    }
    
    await page.reload();
    
    // 카운트 확인
    await expect(page.locator('.comments-title .badge')).toContainText('3');
  });

  test('로그인하지 않은 사용자 - 댓글 폼 숨김', async ({ page }) => {
    // 로그아웃
    await page.goto('/board');
    await page.click('button[type="submit"]:has-text("로그아웃")');
    
    // 게시글로 다시 이동
    await page.click('.post-card a').first();
    
    // 댓글 폼 없음
    await expect(page.locator('#commentForm')).not.toBeVisible();
    
    // 로그인 안내 메시지
    await expect(page.locator('.alert-info')).toContainText('로그인이 필요합니다');
  });
});
