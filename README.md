목차


# Social Feed?
소셜 미디어 통합 Feed 서비스
* 인스타그램, 스레드, 페이스북, 트위터 등 여러 SNS에 게시된 특정 해시태그를 기반으로, 관련 게시물을 한 곳에서 통합적으로 확인할 수 있는 웹서비스
* 사용자는 통합된 게시물들을 상세 조회, 공유, 좋아요할 수 있으며, 원하는 해시태그를 검색/조회 가능
* 즉, 하나의 채널로 유저, 또는 브랜드 의 SNS 노출 게시물 및 통계 확인 가능

## 개발 기간
2024.08.20 - 2024.08.26

## 팀 구성 및 역할
|이름|역할|gitHub|
|------|---|---|
|김아리|회원 가입|https://github.com/lielocks|
|김유진|게시물 공유|https://github.com/TirTir|
|윤채영|게시물 목록 조회|https://github.com/ae-chae|
|임채민|게시물 상세 조회|https://github.com/cmleem|
|최미선|jwt|https://github.com/developerchoims|
|최서경|통계|https://github.com/stringbuckwheat|

## 개발 환경
* `Spring Boot(3.3)`
  * Spring Security
  * JWT
* `JPA/Hibernate`, `QueryDsl`
* `MySQL(9.0)`, `Redis`

## ERD
![image](https://github.com/user-attachments/assets/83479aee-5754-468e-932b-877c60074641)

## 컨벤션
[코딩 컨벤션] https://github.com/PreOnboarding-Team-11/socialFeed/wiki/%E2%9C%85-Github-Convention

# 통계 기능📊
* `해시태그`, `시작일`, `종료일`을 기반으로 `게시글 수`, `조회수`, `좋아요 수`, `공유 수` 등의 통계를 `일별`/`시간별`로 필터링하여 제공하는 기능 

![statistic_query_param](https://github.com/user-attachments/assets/c306e657-ee72-4176-8815-388943eebb35)


* **Repository**: 동적 쿼리, DTO Projection
    * QueryDsl로 **동적 쿼리**를 구현하여 유연한 쿼리 생성 
      * `getDateTemplateByStatisticType` 메서드를 통해 요청된 통계 유형(일별/시간별)에 적절한 날짜 템플릿 동적으로 생성
      * `getExpression` 메서드에서 통계 타입(게시글 수, 좋아요 수 등)에 적절한 JPA 표현식 반환
        * `article.count().intValue()`, `article.likeCount.sum()` 등
    * **DTO Projection**으로 쿼리 결과를 직접 DTO로 변환, 불필요한 데이터 전송을 줄이고 성능 최적화
     

* **Service**: 요구사항에 의거하여 데이터가 존재하지 않는 날짜/시간이 누락되지 않도록 `통계값 0` 설정
  * Repository에서 통계 데이터를 조회한 후,
  * `ChronoUnit`을 사용하여 요청에 맞는 시간/날짜 리스트 생성
  * 생성된 날짜/시간 리스트와 조회된 통계 데이터를 결합하여 최종 결과 매핑 

![statistic_test](https://github.com/user-attachments/assets/f87cdd16-00a7-4587-9069-46e89bbe0a8a)
  * (예외 메시지 클래스 제외) 테스트 커버리지 100% 달성

## 💡 트러블슈팅
### 복잡한 기본값, 예외 로직이 필요한 요청 DTO 생성하기
* 배경: StatisticRequest (통계 요청 DTO) 생성 시 복잡한 기본값, 예외 로직이 요구됨

<details>
    <summary>복잡한 기본값, 예외 로직</summary>
    * 복잡한 기본값
        * hashtag 필드가 없다면 요청자의 username 사용
        * 통계 시작일이 없다면 오늘 기준 일주일 전으로 설정 
        * 통계 종료일이 없다면 오늘로 초기화
    * 예외 처리
        * 통계 시작일이 종료일보다 미래인 경우
        * 일별 통계에서 30일을 초과한 경우
        * 시간대별 통계에서 7일을 초과한 경우
</details>

* 문제: 컨트롤러에 DTO 생성 로직, 비즈니스 로직이 혼재되는 상황 발생 
    * `Builder`와 `삼항연산자`를 사용하여 객체 생성, 이후 `컨트롤러`에서 **예외처리** 수행 
        * 유효하지 않은 요청 데이터를 서비스 레이어로 넘겨주고 싶지 않았기 때문 
    * 그러나 **컨트롤러에 DTO 생성 로직, 비즈니스 로직이 혼재**되어 코드의 명확성이 떨어짐  
    
* 해결: `정적 팩토리 메소드`를 사용하여 리팩토링
  * `new` 연산자를 사용하지 않은 이유
    * 생성자에 기본값 처리 로직이 있는지, 예외 처리 로직이 존재하는지 알리기 어렵다고 판단
  * `createWithDefaults`라는 메서드 이름으로 기본값 로직이 포함되어 있음을 명확히 전달 
  * 기타 생성자의 접근제어자를 private으로 설정하여, `createWithDefaults`로 유효한 데이터를 포함하는 DTO만 생성 가능하도록 강제 
