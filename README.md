# 맛집갤러리
 
## 개요 overview

__맛집갤러리__ 는 본인이 즐겨 다니거나 인상깊었던 맛집의 정보를 리스트의 형태로 저장할 수 있는 안드로이드 애플리케이션입니다.
식당에 방문한 날짜, 식당 이름, 이미지, 간단한 메모 등을 하나의 정보로 저장할 수 있으며, 위치 지정을 위해 gps를 통한 지도 인터페이스를 사용할 수 있습니다.
이렇게 저장된 정보들은 (사진, 이름, 카테고리, 별점)의 간단한 정보로 표시한 틀을 토대로 리스트 형태로 표시됩니다. 여기서 각각의 정보를 눌러서 저장한 정보를 확인할 수 있고,
수정 버튼을 눌러 내용을 변경할 수 있습니다. 사진을 따로 저장하지 않은 정보에는 기본 이미지가 제공됩니다. 각 앱이 어떤 방식으로 이러한 기능들은 제공하는지는 아래의 몇몇 스크린샷 이미지로 확인하실 수 있습니다.

이러한 저장 내용들을 언제든지 백업해놨다가 다시 가져올 수 있게 하기 위해서, 이 앱을 사용할 때는 구글 계정을 통해 로그인을 해야하도록 조치했습니다. 이에 따라 회원탈퇴를 하지 않았다면 나중에 앱을 다시 깔거나 다른 기기로 변경했더라도 언제든지 기존에 사용하던 정보를 가져올 수 있습니다.

이런 기능을 제공하기 위해서는 정보를 저장하기 위한 데이터베이스, 각 저장된 정보들을 백업하고 불러오기 위한 계정 연동, 식당 위치 검색을 위한 네트워크 검색 API 등이 필요했습니다. 이에 따라 Firebase의 구글 계정 연동과 데이터베이스, 로컬 DB를 위한 room 데이터베이스, Retrofit2을 통한 카카오 맵 API 통신 등을 활용했습니다.
각 코드의 구조와 내부 사용 기술들, 그리고 그 목적 등은 아래에서 자세히 후술합니다.


__MyRestaurantGallery__ is an android application which allows you to save impressive restaurant information that you know as list forms.
You can save data such as date, name, image, and simple memo as one information item, and you can use GPS on the map interface for getting location.
This information are displayed as simple form list that have (image, name, category, rate) in it. You can show each information by clicking each item in the list, or edit saved information by clicking the edit button. If you don't designate a proper image for your item, the app provides default images automatically. You can check how those functions work in the real app environment with screenshot images down below :)

In order to back up and restore those saved contents, I make the app user must login for using this app by google authentications. Thanks to that, you can restore your old saved data when you install this app again later except the situation that you already withdrew your account.

For providing those functions to users, I needed database for saving information, authentication system for back up those data, and network searching API for location search of restaurants. So I applied authentication and database of Firebase, room database for local DB, and kakao map API with Retrofit2 interface on this app.
You can check structure of code, used API for this app, each purpose of API, etc down below.


## 사용 방법 How to use

### 앱 사용

데이터와 이미지를 백업하기 위해 사용한 파이어베이스의 요금제로 인해 따로 구글 플레이 스토어에 배포하지 않음.

Sadly, I can't release this app on google play store because I use Firebase for back up data and images. (Firebase is not free for big data)

### 앱 화면 예시

|로그인(백업)|리스트 확인|항목 생성|
|------|---|---|
|![재로그인](https://user-images.githubusercontent.com/70795841/225346940-7ef6cc09-d2b7-44e7-bfe5-8f093f083d84.gif)|![리스트확인](https://user-images.githubusercontent.com/70795841/225330825-b79d0d2b-f076-4483-97d6-26f341a28648.gif)|![항목생성](https://user-images.githubusercontent.com/70795841/225334464-5329031b-6957-47c0-b4d8-6948c01bf0c1.gif)|



|지도로 확인|식당 검색|다크 모드 변경|
|------|---|---|
|![지도확인](https://user-images.githubusercontent.com/70795841/225331008-d522dfca-6e16-4f59-9a3e-fc4dba3e1c84.gif)|![지도검색](https://user-images.githubusercontent.com/70795841/225330924-8465de1e-9a1d-42ba-9c40-2607c86192da.gif)|![다크모드](https://user-images.githubusercontent.com/70795841/225330680-1e9feb27-a830-4c81-827a-5967469558dd.gif)|



## 코드 구조와 기술 Code structure and API

### 코드 개요
* 코딩언어로는 코틀린 활용. (앱 버전 -> 최소 버전 26, 타겟 버전 33)
* 간단한 기능을 제공하는 앱의 특성상, 컴포넌트로는 다수의 액티비티만 사용함.
* 맛집의 위치 지정을 편하게 할 수 있도록 지도 및 위치 검색 기능을 추가함. 이를 위해 Retrofit2를 통해 구글 및 카카오 맵 API를 활용함.
* 구글 계정 연동 및 데이터 저장을 위해서는 구글이 제공하는 FireBase의 Authentication, FireStore, Storage를 활용함.
* 이미지 로드를 위해서는 Glide 모듈을 사용 -> 2022.12 Coil 라이브러리를 통한 로드로 변경함. (여러 이미지 라이브러리 활용 경험을 위함)
* 네트워크 작업과 데이터베이스 작업을 위한 비동기 로직은 코틀린 코루틴을 활용함.

* 2023.01 추가 -> User, Item, Map, Location에 대한 데이터 로직을 ViewModel과 Repository로 분리.
				 MVVM 패턴, Repository 패턴 추가 활용


### 구성 액티비티

![액티비티 구조](https://user-images.githubusercontent.com/70795841/216903647-16f6fb5f-bb94-48e1-aadd-f7e01eedc879.PNG)

* __MainActivity__: 시작 및 메인 액티비티로서 항목 확인, 메뉴를 통한 항목 추가나 로그아웃 등의 행위가 가능하다.
* __LoginActivity__: 로그인이 가능한 액티비티이며 구글 로그인 기능을 제공한다. 로그인 상태가 아닐 시에만 앱 시작할 때 나타난다.
* __AddActivity__: 항목을 추가 혹은 수정하고자 할 때 나타나는 화면을 담당하며, 날짜나 식당 이름, 지역 등의 정보를 추가하여 저장할 수 있다.
* __RecordActivity__: 이미 저장되어 있는 항목을 확인할 때 나타나며, AddActivity와 유사하지만 수정 기능을 제공하지 않는다.
* __MapActivity__: 구글 맵 API를 사용하여 지도를 확인할 수 있고, GPS를 통해 현재 위치를 선택하는 것도 가능하다. 더하여 record 모드로 변환도 가능하다.
* __LocationListActivity__: 카카오 맵 API를 사용하여 식당의 이름을 검색 및 선택할 수 있으며, MapActivity에서 버튼을 눌러 진입할 수 있다.


### 코드 레이어

![코드 아키텍처](https://user-images.githubusercontent.com/70795841/213847084-2a92b974-9b6b-460e-a65b-b01946a794e3.PNG)

코드의 디자인패턴은 View, ViewModel, Model로 분리되는 MVVM 구조를 기본적으로 사용했습니다. 여기에 Repository 패턴을 추가하여 비즈니스 로직 부분을 더 세부화했습니다.

___ViewModel___

이 프로젝트에서 사용된 데이터는 크게 유저, 아이템(식당 정보), 지도, 위치 검색과 관련이 있습니다. 초기 앱 버전에서는 이러한 데이터의 처리를 해당하는 액티비티 코드 내부에서 처리했습니다. 하지만 이에 따라 액티비티가 맡은 역할이 비대해져서 코드의 세부 사항을 수정할 때 어려움이 생긴다는 것을 느꼈습니다. 이에 따라 각 데이터 처리 부분을 대신 처리해주는 뷰모델을 만들어서 각 객체의 역할을 보다 더 명확하게 하고자 했습니다.

ViewModel은 위에서 말한 데이터 종류에 따라 4가지로 나뉩니다. 액티비티들은 화면에서 필요로 하는 데이터에 따라 해당하는 뷰모델 객체의 데이터를 관찰합니다. 사용자의 입력에 따라 데이터를 처리해야 할 일이 생기면 액티비티에서 뷰모델에게 작업을 요청하고 뷰모델은 그 작업을 실행합니다. 액티비티는 데이터의 변화를 관찰하고 변경 내용을 가져옵니다.

이러한 기능 수행을 위해 Android AAC의 ViewModel, LiveData를 활용하였으며 일부 액티비티에서는 자동적인 데이터 표시를 위해 Databinding을 활용했습니다.
엄밀히 따지면 AAC의 ViewModel은 MVVM의 뷰모델을 의미하지 않지만, 별개의 생명주기로 동작하는 특성상 사용을 하기에 적합하다고 판단했습니다.


___Repository___

뷰모델은 실질적인 비즈니스 로직 처리를 담당하기 때문에 데이터를 다루는 모델 부분과 접촉할 필요가 있습니다. 이 앱에서는 해당 데이터를 위해서 이미지대로 Firebase, Room, GoogleMap, Retrofit + Kakao Map API를 사용했습니다. 하지만 때에 따라 모델을 위해 사용하는 API를 수정할 시 Activity가 관찰하는 뷰모델의 코드도 함께 변경하는 것은 액티비티에도 영향을 미칠 수 있을 것이라 생각했습니다. 

따라서 뷰와 인터페이스의 형식으로 연결되는 repository를 만들어서, 혹시나 새로운 API를 사용하게 되더라도 해당 인터페이스로 리포지토리를 구현하여 view와 viewmodel에는 영향이 가지 않도록 조치했습니다.

위 이미지에 묘사된 점선으로 둘러 쌓인 리포지토리들은 모두 인터페이스입니다. 다만 item repository는 이미지와 그 외 데이터를 분리해서 관리해야하는 아이템의 특성 상 인터페이스가 아니라 직접적으로 연결되도록 했으며, 대신 내부에서 사용된 data, image 리포지토리를 인터페이스로 조치했습니다.


___Model___

여기 설명에서 모델은 리포지토리에서 사용되는 API를 의미합니다. 이들이 제공하는 데이터는 각 인터페이스를 구현한 세부 리포지토리에서 최초 활용합니다. 추후 다른 API를 활용할 가능성도 있으며, 이때는 리포지토리 인터페이스를 구현한 새로운 클래스를 만드는 방식으로 이를 구현할 수 있을 것입니다.

User를 위해서는 Firebase의 Authentication 기능, 더 정확히는 구글 계정 연동을 사용합니다. 따라서 signInIntent와 같은 구글 제공 API를 로그인, 로그아웃, 회원 탈퇴를 위해 사용합니다. 예외적으로 로그인 유지 확인은 shared preference를 이용했습니다.

Item을 위해서는 Firebase의 firestore와 storage, 안드로이드 AAC의 room Database와 자바 File을 통한 로컬 저장소 이미지 저장을 사용합니다. Firebase는 구글 계정과 연동하여 사용자의 작성 데이터를 백업하기 위해서 사용하며, 네트워크가 연결되지 않는 상황 등을 대비하여 앱 사용 시에는 room과 로컬 저장소의 데이터를 활용하는 것입니다.

Map을 위해서는 Google Map API를 활용합니다. 무료로 기능을 제공할 뿐만 아니라, 내부 API 사용 측면에서도 공개되어 있는 정보가 많기 때문에 이 API를 선택했습니다. 여기서는 GPS를 통한 현재 위치(위도, 경도), 현재 주소를 얻어올 수 있습니다.

Location Search를 위해서는 Kakao Map API를 활용하며, 통신을 위해서는 Retrofit2 API를 사용했습니다. Map을 위한 API와 다른 것은 검색 기능 구현을 위해 참고할 수 있는 가장 좋은 글이 Kakao 제공 API를 사용하고 있었기 때문입니다. 앱이 필요로 하는 것은 검색 키워드에 따른 결과를 받아오는 것만 있기 때문에, 카테고리를 식당, 카페로 한 GET 요청을 사용하고 이에 대한 응답을 받기 위한 클래스들을 활용합니다.
