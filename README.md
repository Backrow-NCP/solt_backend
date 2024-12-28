# ✈️ SOLT - 맞춤형 여행 일정 추천 & 공유 플랫폼
SOLT는 AI 기술을 활용해 여행자의 성향과 선호도를 분석하고, 개개인에게 최적화된 여행 일정을 추천하는 플랫폼입니다.
생성된 여행 일정은 게시글에 업로드해 다른 사용자와 경험을 공유할 수 있습니다.

<img src="https://github.com/user-attachments/assets/5717634d-38c6-4f35-9e3b-428c775dbbe8" width="70%">

## 📂 **목차**
1. [프로젝트 소개](#-프로젝트-소개)
    - 1.1 [개발 기간](#-개발-기간)
    - 1.2 [개발 인원 및 역할](#-개발-인원-및-역할)
    - 1.3 [개발 환경](#️-개발-환경)
2. [주요 기능](#-주요-기능)
3. [시스템 아키텍처](#️-시스템-아키텍처)
4. [UML 다이어그램](#-UML-다이어그램)
    - 4.1 [Use Case Diagram](#use-case-diagram)
    - 4.2 [Sequence Diagram](#sequence-diagram)
    - 4.3 [Class Diagram](#class-diagram)
5. [API 명세서](#-API-명세서)
6. [기타사항](#-기타사항)

## 📌 프로젝트 소개
SOLT는 AI 기반의 맞춤형 여행 일정 추천 및 공유 플랫폼입니다.
여행자의 성향을 분석해 최적의 일정을 추천하고, 이를 게시글 형태로 공유할 수 있습니다.

<!-- 🔗 SOLT 공식 웹사이트 -->

🔗 [SOLT Frontend Repository ](https://github.com/Backrow-NCP/solt-frontend)

#### 📅 개발 기간
2024.08.28 ~ 2024.10.25

#### 👨‍💻 개발 인원 및 역할
| 이름              | 역할             | 담당 서비스              | GitHub                                                                       |
| --------------- | -------------- | ------------------- | ---------------------------------------------------------------------------- |
| **임수한** (조장)    | Backend, AI    | AI 여행 일정 추천         | [@NoskeLim](https://github.com/orgs/Backrow-NCP/people/NoskeLim)             |
| **박상도** (BE 팀장) | FullStack      | 게시판 관리, 여행 일정 서포트   | [@SD-PARK](https://github.com/orgs/Backrow-NCP/people/SD-PARK)               |
| **박지수**         | Backend, CI/CD | 회원 관리               | [@JisuPark9191](https://github.com/orgs/Backrow-NCP/people/JisuPark9191)     |
| **윤영훈**         | FullStack      | 여행 유형 검사            | [@YeongHunYun](https://github.com/orgs/Backrow-NCP/people/YeongHunYun)       |
| **권정현** (FE 팀장) | Frontend       | 메인, 여행 일정           | [@zvnghyvn](https://github.com/orgs/Backrow-NCP/people/zvnghyvn)             |
| **김성훈**         | Frontend       | 게시판 관리              | [@seonghunkim724](https://github.com/orgs/Backrow-NCP/people/seonghunkim724) |
| **김유나**         | Frontend, AI   | 회원 관리, AI 챗봇 여행 가이드 | [@YooonaKim](https://github.com/orgs/Backrow-NCP/people/YooonaKim)           |

#### 🛠️ 개발 환경
- **Frontend**: React (v18.3.1), Nginx  
- **Backend**: SpringBoot (v2.6.15)  
- **Database**: MySQL (v8.0.4), Redis (v7.4.1)  
- **CI/CD**: Jenkins, Docker  
- **API & 라이브러리**: HyperCLOVA X, Google Map API, Axios (v1.7.7), Lombok
- **IDE**: IntelliJ IDEA Ultimate, VS Code  

## 📑 주요 기능 
주요 기능은 현재 작성 중입니다.
#### 1. AI 여행 일정 추천  
- 사용자가 입력한 **기간, 지역, 테마, 선호 장소** 등을 기반으로 AI가 맞춤형 일정을 생성합니다.  
- **여행 일정 자동 생성 알고리즘**을 개발하고 최적화했습니다.  

#### 2. 여행 일정 및 게시글 공유  
- 생성된 여행 일정을 게시글로 **공유 및 수정**할 수 있으며, 다른 사용자와 경험을 나눌 수 있습니다.  
- 게시글에는 **사진 및 후기**를 추가해 여행 경험을 보다 생생하게 전달합니다.  

#### 3. AI 여행 가이드  
- AI 챗봇이 여행 관련 상담을 제공합니다.  
- **실시간 대화**로 여행 가이드와 소통하고, AI 기반 **여행 페르소나** 시스템을 적용했습니다.  

#### 4. 여행 유형 검사  
- 간단한 문답으로 사용자의 **여행 성향**을 분석합니다.  
- 결과에 따라 **맞춤형 여행지 및 코스**를 자동 추천합니다.  

## 🏗️ 시스템 아키텍처
시스템 아키텍처는 현재 작성 중입니다.

## 📊 UML 다이어그램
UML 다이어그램은 현재 작성 중입니다.
#### Use Case Diagram
_시스템의 주요 사용 사례를 표현한 다이어그램입니다._

#### Sequence Diagram
- **회원 관리** 
- **여행 일정**  
- **게시판 관리** 

#### Class Diagram
_시스템의 객체와 관계를 정의한 클래스 다이어그램입니다._
<!--
_⚠️ 클래스 다이어그램은 크기가 커서 PDF로 제공합니다._  
👉 **클래스 다이어그램 보기 (다운로드 링크)**  _
-->

## 🧑‍💻 API 명세서
API 명세서는 현재 작성 중입니다.

## 🚧 기타사항
- 프로젝트는 지속적으로 **기능 개선 및 유지 보수**가 진행됩니다.
- 코드 및 문서 기여를 적극 환영하며, Pull Request 및 Issue를 통해 자유롭게 의견을 남겨주세요.
