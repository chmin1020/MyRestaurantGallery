# 맛집갤러리
 
## 개요 overview

맛집갤러리는 본인이 즐겨 다니거나 인상깊었던 맛집의 정보를 리스트의 형태로 저장할 수 있는 안드로이드 애플리케이션입니다.
식당에 방문한 날짜, 식당 이름, 위치, 이미지, 간단한 메모 등을 하나의 정보로 저장할 수 있으며, 위치 지정을 위해 gps를 통한 지도 인터페이스를 사용할 수 있습니다.
이렇게 저장된 정보들은 (사진, 이름, 카테고리, 별점)의 간단한 정보로 표시한 틀을 토대로 리스트 형태로 표시됩니다. 여기서 각각의 정보를 눌러서 저장한 정보를 확인할 수 있고,
수정 버튼을 눌러 내용을 변경할 수 있습니다. 사진을 따로 저장하지 않은 정보에는 기본 이미지가 제공됩니다. 각 앱이 어떤 방식으로 이러한 기능들은 제공하는지는 아래의 몇몇 스크린샷 이미지로 확인하실 수 있습니다.

이러한 저장 내용들을 언제든지 백업해놨다가 다시 가져올 수 있게 하기 위해서, 이 앱을 사용할 때는 구글 계정을 통해 로그인을 해야하도록 조치했습니다. 이에 따라 회원탈퇴를 하지 않았다면 나중에 앱을 다시 깔거나 다른 기기로 변경했더라도 언제든지 기존에 사용하던 정보를 가져올 수 있습니다.

이런 기능을 제공하기 위해서는 정보를 저장하기 위한 데이터베이스, 각 저장된 정보들을 백업하고 불러오기 위한 계정 연동, 식당 위치 검색을 위한 네트워크 검색 API 등이 필요했습니다. 이에 따라 Firebase의 구글 계정 연동과 데이터베이스, 로컬 DB를 위한 room 데이터베이스, Retrofit2을 통한 카카오 맵 API 통신 등을 활용했습니다.
각 코드의 구조와 내부 사용 기술들, 그리고 그 목적 등은 아래에서 자세히 후술합니다.


MyRestaurantGallery is an android application which allows you to save impressive restaurant information that you know as list forms.
You can save data such as date, name, location, image, and simple memo as one information item, and you can use GPS on the map interface for getting location.
This information are displayed as simple form list that have (image, name, category, rate) in it. You can show each information by clicking each item in the list, or edit saved information by clicking the edit button. If you don't designate a proper image for your item, the app provides default images automatically. You can check how those functions work in the real app environment with screenshot images down below :)

In order to back up and restore those saved contents, I make the app user must login for using this app by google authentications. Thanks to that, you can restore your old saved data when you install this app again later except the situation that you already withdrew your account.

For providing those functions to users, I needed database for saving information, authentication system for back up those data, and network searching API for location search of restaurants. So I applied authentication and database of Firebase, room database for local DB, and kakao map API with Retrofit2 interface on this app.
You can check structure of code, used API for this app, each purpose of API, etc down below.


## 코드 구조와 기술 Code structure and API

**구성 액티비티**

![액티비티 구조](https://user-images.githubusercontent.com/70795841/213671439-c58c5c70-569e-4cf2-afb2-6b5e52a65fea.PNG)

* __LoginActivity__: 시작 액티비티이며 구글 로그인 기능을 제공한다. 이미 로그인 정보가 있다면 바로 Main 화면으로 넘어간다.
* __MainActivity__: 메인 액티비티로서 항목 확인, 메뉴를 통한 항목 추가나 로그아웃 등의 행위가 가능하다.
* __AddActivity__: 항목을 추가 혹은 수정하고자 할 때 나타나는 화면을 담당하며, 날짜나 식당 이름, 지역 등의 정보를 추가하여 저장할 수 있다.
* __RecordActivity__: 이미 저장되어 있는 항목을 확인할 때 나타나며, AddActivity와 유사하지만 수정 기능을 제공하지 않는다.
* __MapActivity__: 구글 맵 API를 사용하여 지도를 확인할 수 있고, GPS를 통해 현재 위치를 선택하는 것도 가능하다.
* __LocationListActivity__: 카카오 맵 API를 사용하여 식당의 이름을 검색 및 선택할 수 있으며, MapActivity에서 버튼을 눌러 진입할 수 있다.

**사용 기술 요소**
* 코딩언어로는 코틀린 활용. (앱 버전 -> 최소 버전 25, 타겟 버전 32)
* 간단한 기능을 제공하는 앱의 특성상, 컴포넌트로는 다수의 액티비티만 사용함.
* 맛집의 위치 지정을 편하게 할 수 있도록 지도 및 위치 검색 기능을 추가함. 이를 위해 Retrofit을 통해 구글 및 카카오 맵 API를 활용함.
* 구글 계정 연동 및 데이터 저장을 위해서는 구글이 제공하는 FireBase의 Authentication, FireStore, Storage를 활용함.
* 이미지 로드를 위해서는 Glide 모듈을 사용 -> 2022.12 Coil 라이브러리를 통한 로드로 변경함.
* 네트워크 작업과 데이터베이스 작업을 위한 비동기 로직은 코틀린 코루틴을 활용함.

* 2023.01 추가 -> User, Item, Map, Location에 대한 데이터 로직을 ViewModel과 Repository로 분리.
				 MVVM 패턴, Repository 패턴 추가 활용


## 사용 방법 How to use

**앱 사용**
데이터와 이미지를 백업하기 위해 사용한 파이어베이스의 요금제로 인해 따로 구글 플레이 스토어에 배포하지 않음.
현재는 코드를 받아서 테스트를 통해서만 실행 가능.

Sadly, I can't release this app on google play store because I use Firebase for back up data and images. (Firebase is not free for big data)
So this app only can be installed by code test for now :(

**앱 화면 예시**
![mainActivity](https://user-images.githubusercontent.com/70795841/193832432-3fce3a07-5e92-4f25-84e4-536e468fffda.jpg)
![mapActivity](https://user-images.githubusercontent.com/70795841/193832437-0779f411-da09-451d-944f-4f9faeecd847.jpg)
![addActivity](https://user-images.githubusercontent.com/70795841/193832441-ec87bd04-b683-457c-bdc7-daf41035fcc0.jpg)
![locationListAcitivity](https://user-images.githubusercontent.com/70795841/193832443-9c3ee110-ec18-4abc-9b56-99a0ce76f324.jpg)

순서대로 메인화면, 지도화면, 항목추가화면, 지역검색화면의 모습

