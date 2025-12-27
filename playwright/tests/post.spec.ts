import { test, expect } from '@playwright/test';
import { generateUsername, generatePostTitle, signup, login, createPost } from './utils';

test.describe('게시글 CRUD 테스트', () => {
  let username: string;
  let postTitle: string;

  test.beforeEach(async ({ page }) => {
    username = generateUsername();
    postTitle = generatePostTitle();
    
    // 회원가입 및 로그인
    await signup(page, username);
    await login(page, username);
  });

  test('게시글 작성 성공', async ({ page }) => {
    await page.goto('/board/write');
    
    // 폼 확인
    await expect(page.locator('h2')).toContainText('새 글 작성');
    
    // 카테고리 선택 (첫 번째)
    await page.check('input[type="checkbox"]').first();
    
    // 제목, 내용 입력
    await page.fill('#title', postTitle);
    await page.fill('#content', '테스트 게시글 내용입니다.');
    
    // 글자수 카운터 확인
    await expect(page.locator('.char-count')).toContainText('/200');
    
    // 제출
    page.once('dialog', dialog => {
      expect(dialog.message()).toContain('작성되었습니다');
      dialog.accept();
    });
    
    await page.click('button[type="submit"]');
    await page.waitForURL('/board');
    
    // 게시글 목록에서 확인
    await expect(page.locator('.post-card').first()).toContainText(postTitle);
  });

  test('게시글 상세 조회', async ({ page }) => {
    // 게시글 작성
    await createPost(page, postTitle, '테스트 내용입니다.');
    
    // 첫 번째 게시글 클릭
    await page.click('.post-card a').first();
    
    // 상세 페이지 확인
    await expect(page.locator('.post-title')).toContainText(postTitle);
    await expect(page.locator('.post-content')).toContainText('테스트 내용입니다.');
    await expect(page.locator('.author-name')).toContainText(username);
    
    // 수정/삭제 버튼 확인 (작성자만)
    await expect(page.locator('a:has-text("수정")')).toBeVisible();
    await expect(page.locator('button:has-text("삭제")')).toBeVisible();
  });

  test('게시글 수정', async ({ page }) => {
    await createPost(page, postTitle, '원본 내용');
    
    // 상세 페이지로 이동
    await page.click('.post-card a').first();
    
    // 수정 페이지로 이동
    await page.click('a:has-text("수정")');
    await expect(page).toHaveURL(/\/board\/\d+\/edit/);
    
    // 기존 값 확인
    await expect(page.locator('#title')).toHaveValue(postTitle);
    await expect(page.locator('#content')).toHaveValue('원본 내용');
    
    // 수정
    await page.fill('#title', postTitle + ' (수정됨)');
    await page.fill('#content', '수정된 내용입니다.');
    
    page.once('dialog', dialog => {
      expect(dialog.message()).toContain('수정되었습니다');
      dialog.accept();
    });
    
    await page.click('button[type="submit"]');
    await page.waitForURL(/\/board\/\d+/);
    
    // 수정 확인
    await expect(page.locator('.post-title')).toContainText('수정됨');
    await expect(page.locator('.post-content')).toContainText('수정된 내용');
  });

  test('게시글 삭제', async ({ page }) => {
    await createPost(page, postTitle, '삭제할 내용');
    
    // 상세 페이지로 이동
    await page.click('.post-card a').first();
    
    // 삭제 확인 다이얼로그
    page.once('dialog', dialog => {
      expect(dialog.message()).toContain('삭제하시겠습니까');
      dialog.accept();
    });
    
    await page.click('button:has-text("삭제")');
    
    // 삭제 완료 알림
    page.once('dialog', dialog => {
      expect(dialog.message()).toContain('삭제되었습니다');
      dialog.accept();
    });
    
    // 목록으로 리디렉션
    await page.waitForURL('/board');
    
    // 삭제된 게시글이 목록에 없는지 확인
    await expect(page.locator('.post-card')).not.toContainText(postTitle);
  });

  test('게시글 검색', async ({ page }) => {
    const searchKeyword = '검색테스트';
    await createPost(page, searchKeyword + ' 제목', '내용입니다');
    
    await page.goto('/board');
    
    // 검색
    await page.fill('.search-input', searchKeyword);
    await page.click('button:has-text("검색")');
    
    // 결과 확인
    await expect(page.locator('.post-card')).toContainText(searchKeyword);
  });

  test('카테고리 필터링', async ({ page }) => {
    await createPost(page, postTitle, '내용', [1]);
    
    await page.goto('/board');
    
    // 카테고리 클릭
    await page.click('.category-card').first();
    
    // URL에 categoryId 파라미터 확인
    await expect(page).toHaveURL(/categoryId=\d+/);
    
    // 필터링된 게시글만 표시
    await expect(page.locator('.post-card')).toBeVisible();
  });

  test('페이지네이션', async ({ page }) => {
    // 여러 게시글 작성 (16개 이상이면 다음 페이지 생김)
    for (let i = 0; i < 16; i++) {
      await createPost(page, `게시글 ${i}`, `내용 ${i}`);
    }
    
    await page.goto('/board');
    
    // 페이지네이션 확인
    await expect(page.locator('.pagination')).toBeVisible();
    
    // 다음 페이지 클릭
    await page.click('.pagination a:has-text("다음")');
    
    // URL에 page 파라미터 확인
    await expect(page).toHaveURL(/page=1/);
  });
});
