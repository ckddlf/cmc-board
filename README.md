## 테스트 결과

프로젝트의 주요 기능을 검증하기 위해 **컨트롤러 테스트**와 **서비스 테스트**를 작성했습니다.  
각 테스트는 `mvn verify` 명령어로 실행되며, 모든 테스트가 성공적으로 완료되었습니다.

### 1. 컨트롤러 테스트

컨트롤러 레이어의 API 동작을 검증한 결과입니다.  
아래 이미지는 `PostControllerTest`와 `CommentControllerTest` 등 컨트롤러 관련 테스트 실행 결과를 보여줍니다.

![Controller Test Result](https://github.com/user-attachments/assets/87201531-7c1c-43c8-89fe-0de4853146f9)

- 테스트 건수: 4개 (PostController: 1, CommentController: 3)  
- 실패/에러: 0  
- 모든 테스트 통과 확인 ✅

---

### 2. 서비스 테스트

서비스 레이어의 비즈니스 로직 검증 결과입니다.  
아래 이미지는 `AuthServiceTest`, `BookmarkServiceTest`, `CategoryServiceTest`, `PostServiceTest`, `CommentServiceTest` 실행 결과입니다.

![Service Test Result](https://github.com/user-attachments/assets/5bdc893d-6b4c-4166-915b-73c2047df526)

- 테스트 건수: 22개  
- 실패/에러: 0  
- 모든 비즈니스 로직 정상 동작 확인 ✅

---

> ✅ **결론:**  
> 모든 컨트롤러 및 서비스 테스트가 성공적으로 수행되어, 현재 구현된 기능들이 의도한 대로 동작함을 확인했습니다.
