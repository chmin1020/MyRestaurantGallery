# 맛집갤러리
 
## 개요 overview

맛집갤러리는 본인이 즐겨 다니거나 인상깊었던 맛집의 정보를 리스트의 형태로 저장할 수 있는 안드로이드 애플리케이션입니다.
식당에 방문한 날짜, 식당 이름, 위치, 이미지, 간단한 메모 등을 하나의 정보로 저장할 수 있으며, 위치 지정을 위해 gps를 통한 지도 인터페이스를 활용할 수 있습니다.

이런 기능을 제공하기 위해서는 정보를 저장하기 위한 데이터베이스, 각 저장된 정보들을 백업하고 불러오기 위한 계정 연동, 식당 위치 검색을 위한 네트워크 검색 API 등이 필요했습니다. 이에 따라 Firebase의 구글 계정 연동과 데이터베이스, Retrofit2을 통한 카카오 맵 API 통신 등을 활용했습니다.



## 코드 구조와 기술

**구성 액티비티**

![액티비티 구조](https://user-images.githubusercontent.com/70795841/213671439-c58c5c70-569e-4cf2-afb2-6b5e52a65fea.PNG)

* LoginActivity: 기본 시작 액티비티이며 구글 로그인 기능을 제공한다. 이미 로그인 정보가 있다면 바로 Main 화면으로 넘어간다.
* MainActivity: 메인 액티비티로서 항목 확인 및 검색, 메뉴를 통해 항목 추가나 로그아웃 등의 행위가 가능하다.
* AddActivity: 항목을 추가하고자 할 때 나타나는 화면을 담당하며, 날짜나 식당 이름, 지역 등의 정보를 추가하여 저장할 수 있다.
* RecordActivity: 이미 저장되어 있는 항목을 확인할 때 나타나며, AddActivity와 유사하지만 수정 기능을 제공하지 않는다.
* MapActivity: 구글 맵 API를 사용하여 지도를 확인할 수 있고, GPS를 통해 현재 위치를 선택하는 것도 가능하다.
* LocationListActivity: 카카오 맵 API를 사용하여 식당의 이름을 검색 및 선택할 수 있으며, MapActivity에서 버튼을 눌러 진입할 수 있다.

**사용 요소**
* 간단한 기능을 제공하는 앱의 특성상, 컴포넌트로는 다수의 액티비티만 사용함.
* 맛집의 위치 지정을 편하게 할 수 있도록 지도 및 위치 검색 기능을 추가함. 이를 위해 Retrofit을 통해 구글 및 카카오 맵 API를 활용함.
* 구글 계정 연동 및 데이터 저장을 위해서는 구글이 제공하는 FireBase의 Authentication, FireStore, Storage를 활용함.
* 2023.01 추가 -> User, Item, Map, Location에 대한 데이터 로직을 ViewModel과 Repository로 분리.
				 MVVM 패턴, Repository 패턴 추가 활용

----------------------------------------------

**앱 화면 예시**

![mainActivity](https://user-images.githubusercontent.com/70795841/193832432-3fce3a07-5e92-4f25-84e4-536e468fffda.jpg)
![mapActivity](https://user-images.githubusercontent.com/70795841/193832437-0779f411-da09-451d-944f-4f9faeecd847.jpg)
![addActivity](https://user-images.githubusercontent.com/70795841/193832441-ec87bd04-b683-457c-bdc7-daf41035fcc0.jpg)
![locationListAcitivity](https://user-images.githubusercontent.com/70795841/193832443-9c3ee110-ec18-4abc-9b56-99a0ce76f324.jpg)

순서대로 메인화면, 지도화면, 항목추가화면, 지역검색화면의 모습