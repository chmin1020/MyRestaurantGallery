# 맛집리스트
 
[Subject] 정보저장 앱

[Contents] 사람들이 자신이 알고 있는 맛집들을 자유롭게 저장하고 다시 찾아볼 수 있는 기능을 제공하는 앱

**구성 액티비티**
* LoginActivity: 기본 시작 액티비티이며 구글 로그인 기능을 제공한다. 이미 로그인 정보가 있다면 바로 Main 화면으로 넘어간다.
* MainActivity: 메인 액티비티로서 항목 확인 및 검색, 메뉴를 통해 항목 추가나 로그아웃 등의 행위가 가능하다.
* AddActivity: 항목을 추가하고자 할 때 나타나는 화면을 담당하며, 날짜나 식당 이름, 지역 등의 정보를 추가하여 저장할 수 있다.
* RecordActivity: 이미 저장되어 있는 항목을 확인할 때 나타나며, AddActivity와 유사하지만 수정 기능을 제공하지 않는다.
* MapActivity: 구글 맵 API를 사용하여 지도를 확인할 수 있고, GPS를 통해 현재 위치를 선택하는 것도 가능하다.
* LocationListActivity: 카카오 맵 API를 사용하여 식당의 이름을 검색 및 선택할 수 있으며, MapActivity에서 버튼을 눌러 진입할 수 있다.
* ProgressActivity: 항목 저장, 회원 탈퇴같은 시간 소요 작업 시 진행을 나타내는 아이콘을 화면에 보여준다.

**사용 요소**
* 간단한 기능을 제공하는 앱의 특성상, 컴포넌트로는 다수의 액티비티만 사용함.
* 맛집의 위치 지정을 편하게 할 수 있도록 지도 및 위치 검색 기능을 추가함. 이를 위해 Retrofit을 통해 구글 및 카카오 맵 API를 활용함.
* 구글 계정 연동 및 데이터 저장을 위해서는 구글이 제공하는 FireBase의 Authentication, FireStore, Storage를 활용함.

---

**앱 화면 예시**

![mainActivity](https://user-images.githubusercontent.com/70795841/193832432-3fce3a07-5e92-4f25-84e4-536e468fffda.jpg)
![mapActivity](https://user-images.githubusercontent.com/70795841/193832437-0779f411-da09-451d-944f-4f9faeecd847.jpg)
![addActivity](https://user-images.githubusercontent.com/70795841/193832441-ec87bd04-b683-457c-bdc7-daf41035fcc0.jpg)
![locationListAcitivity](https://user-images.githubusercontent.com/70795841/193832443-9c3ee110-ec18-4abc-9b56-99a0ce76f324.jpg)

순서대로 메인화면, 지도화면, 항목추가화면, 지역검색화면의 모습