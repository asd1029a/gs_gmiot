#로컬 개발 환경 시작 하기

### 준비사항

* 개발 환경
  * Java 8 이상 설치(앞으로는 11로 가야함...)
  * maven 설치
  * 환경설정에 JAVA_HOME, MAVEN_HOME 설정
    * java -version -> 1.8 이상
    * mvn -version -> 3.x 이상
  * lombok 설치
* 개발 툴
  * Eclipse STS
  * InteliJ CE(권장)

---
### 소스 내려 받기 / 빌드

* 계정 만들기
  * http://172.20.5.3/users/sign_in
* git clone http://172.20.5.3/webapp/danusys-guardian-parent.git
* 전체 소스 빌드(1,2 둘중 하나만 하면 됨)
  * 방법 1 : 콘솔창 (alt + F12)
    * mvn clean install -f pom.xml -Dmaven.test.skip=true
  * 방법 2 : Maven 사이드 툴바 실행( 상단 번개모양 클릭(test skip)) > install 실행

![Maven 사이드바 실행](doc/02-Maven-tool-bar.png)

---

### 로컬 서버 실행
* 프로퍼티 파일 내 DB 접속 정보 확인
  * application-local.properties / application-dev.properties
* Edit Configurations > Modify Options > Add VM options 항몽 추가
  * -Dspring.profiles.active=local 추가

![Active profile 추가 방법](doc/01-edit-configurations.png)

* Run
  * 상단 네비게이션
  
  ![Server start](doc/03-Server-start.png)

  * 하단 탭 (alt+8)
  
  ![Server start](04-Server-start.png)


