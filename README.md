# 맛집갤러리
 
## 개요 overview

__맛집갤러리__ 는 본인이 좋아하는 맛집의 정보를 리스트의 형태로 저장할 수 있는 안드로이드 애플리케이션입니다.

<p align="center"><img src="https://user-images.githubusercontent.com/70795841/225352998-d5b82130-1fd5-4848-974c-7718ee5f0cbd.PNG" width="700" height="350"></p>

맛집갤러리에서는 식당에 방문한 날짜, 식당 이름, 이미지, 간단한 메모 등을 하나의 정보로 저장할 수 있으며, 위치 지정을 위해 검색을 지원하는 지도 인터페이스를 사용할 수 있습니다.
이렇게 저장된 정보는 (사진, 이름, 카테고리, 별점)으로 정리한 간단한 틀을 토대로 리스트 형태로 표시됩니다. 여기서 각각의 정보를 눌러서 저장한 정보를 확인할 수 있고,
수정 버튼을 눌러 내용을 변경할 수 있습니다. 사진을 따로 저장하지 않은 정보에는 식당 카테고리에 따른 기본 이미지가 제공됩니다. 각 앱이 어떤 방식으로 이러한 기능들은 제공하는지는 아래의 몇몇 gif 이미지로 확인하실 수 있습니다.

이러한 저장 내용들을 언제든지 백업해놨다가 다시 가져올 수 있게 하기 위해서, 이 앱을 사용할 때는 구글 계정을 통해 로그인을 해야하도록 조치했습니다. 이에 따라 회원탈퇴를 하지 않았다면 나중에 앱을 다시 깔거나 다른 기기로 변경했더라도 언제든지 기존에 사용하던 정보를 가져올 수 있습니다.

이런 기능을 제공하기 위해서는 정보를 저장하기 위한 데이터베이스, 각 저장된 정보들을 백업하고 불러오기 위한 계정 연동, 식당 위치 검색을 위한 네트워크 검색 API 등이 필요했습니다. 이에 따라 Firebase의 구글 계정 연동과 데이터베이스, 로컬 DB를 위한 room 데이터베이스, Retrofit2을 통한 카카오 맵 API 통신 등을 활용했습니다.
각 코드의 구조와 내부 사용 기술들, 그리고 그 목적 등은 아래에서 자세히 후술합니다.


__MyRestaurantGallery__ is an android application which allows you to save impressive restaurant information that you know as list forms.
You can save data such as date, name, image, and simple memo as one information item, and you can use map interface with search function for getting location.
This information are displayed as simple form list that have (image, name, category, rate) in it. You can show each information by clicking each item in the list, or edit saved information by clicking the edit button. If you don't designate a proper image for your item, the app provides proper default images automatically by category selection. You can check how those functions work in the real app environment with gif images down below :)

In order to back up and restore those saved contents, I make the app user must login for using this app by google authentication. Thanks to that, you can restore your old saved data when you install this app again later except the situation that you already withdrew your account.

For providing those functions to users, I needed database for saving information, authentication system for back up those data, and network searching API for location search of restaurants. So I applied authentication and database of Firebase(Firestore and Storage), room database for local DB, and kakao map API with Retrofit2 interface on this app.
You can check structure of code, used API for this app, each purpose of API, etc down below.


## 사용 방법 How to use

### 앱 사용

데이터와 이미지를 백업하기 위해 사용한 파이어베이스의 요금제로 인해 따로 구글 플레이 스토어에 배포하지 않음.

Sadly, I can't release this app on google play store because I use Firebase for back up data and images. (Firebase is not free for big data)

### 앱 화면 예시

|로그인(백업)|리스트 확인|항목 생성|
|------|---|---|
|![재로그인](https://user-images.githubusercontent.com/70795841/225350844-c2442078-a844-4e43-8a82-ffc72383b395.gif)|![리스트확인](https://user-images.githubusercontent.com/70795841/225330825-b79d0d2b-f076-4483-97d6-26f341a28648.gif)|![항목생성](https://user-images.githubusercontent.com/70795841/225334464-5329031b-6957-47c0-b4d8-6948c01bf0c1.gif)|

- 외부 DB를 통해 효과적으로 데이터를 백업하여, 재로그인 시 기존 정보를 불러올 수 있습니다.
- 메인 화면에서 자연스러운 전환효과와 함께 저장된 각 리스트를 확인해볼 수 있습니다.
- 직접 찍은 사진, 아니면 기본 제공하는 이미지와 함께 나만의 식당 정보를 깔끔하게 정리해서 저장할 수 있습니다.


|지도로 확인|식당 검색|다크 모드 변경|
|------|---|---|
|![지도확인](https://user-images.githubusercontent.com/70795841/227405104-8086b6f2-9a22-49f1-a80f-61381352a590.gif)|![지도검색](https://user-images.githubusercontent.com/70795841/227404406-2498a02e-33e8-4169-9c0f-1e6c94f8da86.gif)|![다크모드](https://user-images.githubusercontent.com/70795841/225330680-1e9feb27-a830-4c81-827a-5967469558dd.gif)|

- 지도 검색을 통해 식당을 지정하면 버튼을 눌러 구글맵을 통한 위치 정보를 제공받을 수 있습니다.
- 지도 화면에서 버튼을 클릭해 찾고 있는 식당을 검색하고 원하는 식당을 선택해서 가져올 수 있습니다.
- 2가지의 테마를 제공하여 사용자 시스템에 맞게 다크모드와 라이트모드 양식을 별도로 제공합니다.


## 코드 구조와 기술 Code structure and API

### 코드 개요
* 코딩언어로는 코틀린 활용. (앱 버전 -> 최소 버전 26, 타겟 버전 33)
* 간단한 기능을 제공하는 앱의 특성상, 컴포넌트로는 다수의 액티비티만 사용함.
* 맛집의 위치 지정을 편하게 할 수 있도록 지도 및 위치 검색 기능을 추가함. 이를 위해 Retrofit2를 통해 구글 및 카카오 맵 API를 활용함.
* 구글 계정 연동 및 데이터 저장을 위해서는 구글이 제공하는 FireBase의 Authentication, FireStore, Storage를 활용함.
* 이미지 로드를 위해서는 Glide 모듈을 사용 -> 2022.12 Coil 라이브러리를 통한 로드로 변경함. (여러 이미지 라이브러리 활용 경험을 위함)
* 네트워크 작업과 데이터베이스 작업을 위한 비동기 로직은 코틀린 코루틴을 활용함.
* 안드로이드 jetpack AAC 요소 활용
	- Room: 네트워크 상황 등을 대비하여 각 데이터는 로컬 DB에도 Room을 사용하여 저장함.
	- ViewModel: UI와 데이터를 최대한 분리하고 UI 돌발 상황에도 안전한 데이터 복원을 할 수 있도록 하기 위해 사용함.
	- Databinding: 특정 액티비티 내 저장된 정보를 표현하는 과정에서 boilerplate 코드를 줄이고 뷰와 데이터가 자동 연결되도록 함.
	- Livedata: ViewModel과 Activity 사이 데이터를 observer 패턴을 적용하여 느슨하게 연결할 수 있도록 하기 위해 사용함.

* 2023.01 추가
	- 데이터 처리와 뷰 표시가 모두 Activity 코드 내에 존재하여 코드 구조가 복잡하고 가독성이 떨어진다고 판단.
	
	  이에 따라 User, Item, Map, Location에 대한 데이터 로직을 별도의 ViewModel과 Repository로 분리

* 2023.03 추가 
	- 안드로이드 공식 문서 가이드에 따라 UI / Domain / Data 구조로 코드 아키텍처를 바라보기 시도.
	  
	  이에 따라 기존의 아키텍처에서 ViewModel이 UI의 역할을 제대로 하지 않고 Repository와 유사한 역할을 하고 있다고 판단.
          
	  따라서 각 뷰가 별도의 뷰모델로 관리되고, Domain에 각 Repository의 필요 기능을 뽑은 Usecase를 도입함.
	
	
	- 코드의 구성요소가 많아짐에 따라 각 요소 간의 결합성도 더 높아진다고 판단. 
	  
	  따라서 hilt 라이브러리를 적용하여 Repository, Usecase, Data(room)을 모듈로 하고 activity, viewMdoel로 의존성 주입.


### 구성 액티비티

![액티비티 구조](https://user-images.githubusercontent.com/70795841/216903647-16f6fb5f-bb94-48e1-aadd-f7e01eedc879.PNG)

* __MainActivity__: 시작 및 메인 액티비티로서 항목 확인, 메뉴를 통한 항목 추가나 로그아웃 등의 행위가 가능하다.
* __LoginActivity__: 로그인이 가능한 액티비티이며 구글 로그인 기능을 제공한다. 로그인 상태가 아닐 시에만 앱 시작할 때 나타난다.
* __AddActivity__: 항목을 추가 혹은 수정하고자 할 때 나타나는 화면을 담당하며, 날짜나 식당 이름 등의 정보를 추가하여 저장할 수 있다.
* __RecordActivity__: 이미 저장되어 있는 항목을 확인할 때 나타나며, AddActivity와 유사하지만 수정 기능을 제공하지 않는다.
* __MapActivity__: 구글 맵 API를 사용하여 지도를 확인할 수 있으며, 검색으로 갈 수 없는 record 모드로 변환도 가능하다.
* __LocationListActivity__: 카카오 맵 API를 사용하여 식당의 이름을 검색 및 선택할 수 있으며, MapActivity에서 버튼을 눌러 진입할 수 있다.


### 코드 레이어

![코드 아키텍처 2](https://user-images.githubusercontent.com/70795841/227266719-0dda6780-b94e-4d43-b443-fc2ab09eec8f.PNG)

* 코드의 디자인패턴은 View, ViewModel로 대표되는 UI 영역과 그 밖의 data source로 대표되는 Data 영역으로 분리하는 방식이었습니다.
* 여기에 Repository 패턴을 추가하여 Data Source의 중구난방한 데이터를 깔끔하게 분류하여 정리했습니다.
* 이 후 Usecase를 통해 각 Repository의 기능을 추출하여 Domain 영역을 담당하게 했습니다.
* 따로 처리할 데이터가 없는 mapActivity를 제외한 나머지 Activity는 각 ViewModel과 연결되고, ViewModel은 적절한 Usecase로 동작을 수행합니다.

___ViewModel___

이 프로젝트에서 사용된 데이터는 크게 유저, 아이템(식당 정보), 지도, 위치 검색과 관련이 있습니다. 초기 앱 버전에서는 이러한 데이터의 처리를 해당하는 액티비티 코드 내부에서 처리했습니다. 하지만 이에 따라 액티비티가 맡은 역할이 비대해져서 코드의 세부 사항을 수정할 때 어려움이 생긴다는 것을 느꼈습니다. 이에 따라 각 데이터 처리 부분을 대신 처리해주는 뷰모델을 만들어서 각 객체의 역할을 보다 더 명확하게 하고자 했습니다.

최초의 ViewModel 구조에서는 ViewModel을 Activity와 조금 더 가까운 Repository처럼 활용했으나, 추가 갱신을 통해 각 Activity가 별도의 뷰모델을 통해 로직을 처리하도록 했습니다. 이는 각 뷰모델의 역할 및 코드 복잡성 자체를 단순화하기 위함입니다. 여기서 겹칠 수 있는 기능은 Usecase로 처리했습니다.

이러한 기능 수행을 위해 Android AAC의 ViewModel, LiveData를 활용하였으며 일부 액티비티에서는 자동적인 데이터 표시를 위해 Databinding을 활용했습니다.

___UseCase___

각 ViewModel은 결국 모델에게 데이터 처리를 요청하고 또 결과를 받아올 필요가 있습니다. 이를 위해 뒤에서 설명하는 Repository를 이용할 수도 있지만 여기서는 추가적인 도메인 영역으로 세부 UseCase를 사용했습니다.이는 두 가지의 목적을 위해서입니다. 


첫째로 userCheck, itemSelect와 같은 재사용되는 로직의 존재입니다. 이를 위해 각 뷰모델이 큰 Repository에 의존성을 가지는 것보다는 각 기능을 담당하는 비즈니스 로직 구현체와 연결되는 것이 기능의 별개 동작 확인 및 유지 보수에 유리할 것이라고 판단했습니다.


둘째로 로그인이나 메인화면 등에서 user, item repository를 모두 사용하는 등 여러 repository가 한 viewModel에서 사용되는 것을 막기 위함입니다. 이는 불가능한 작업은 아니지만 여러 repository와의 연결은 자칫 코드의 복잡성을 높일 수 있기에 이를 대신 결합해서 기능을 구현한 하나의 usecase를 사용하기로 한 것입니다.

___Repository___

이 앱에서는 필요한 데이터를 위해서 이미지대로 Firebase, Room, Retrofit + Kakao Map API를 사용했습니다. 하지만 각 데이터 소스가 완전히 별개로 존재하기 때문에 코드 구현 시 복잡성이 증가할 우려가 있었습니다. 또한 때에 따라 모델을 위해 사용하는 API를 수정할 시 Activity가 관찰하는 뷰모델의 코드도 함께 변경하는 것은 액티비티에도 영향을 미칠 수 있을 것이라 생각했습니다. 


따라서 인터페이스의 형식으로 연결되는 repository를 만들어서, 혹시나 새로운 API를 사용하게 되더라도 해당 인터페이스로 리포지토리를 구현하여 view와 viewmodel, 즉 UI 영역에는 영향이 가지 않도록 조치했습니다.


위 이미지에 묘사된 점선으로 둘러 쌓인 리포지토리들은 모두 인터페이스입니다. 다만 item repository는 이미지와 그 외 데이터를 분리해서 관리해야하는 아이템의 특성 상 인터페이스가 아니라 직접적으로 연결되도록 했으며, 대신 내부에서 사용된 data, image 리포지토리를 인터페이스로 조치했습니다.


___Data Source___

여기 설명에서 Data Source는 리포지토리에서 사용되는 API를 의미합니다. 이들이 제공하는 데이터는 각 인터페이스를 구현한 세부 리포지토리에서 최초 활용합니다. 추후 다른 API를 활용할 가능성도 있으며, 이때는 리포지토리 인터페이스를 구현한 새로운 클래스를 만드는 방식으로 이를 구현할 수 있을 것입니다.


User를 위해서는 Firebase의 Authentication 기능, 더 정확히는 구글 계정 연동을 사용합니다. 따라서 signInIntent와 같은 구글 제공 API를 로그인, 로그아웃, 회원 탈퇴를 위해 사용합니다. 예외적으로 로그인 유지 확인은 shared preference를 이용했습니다.


Item을 위해서는 Firebase의 firestore와 storage, 안드로이드 AAC의 room Database와 자바 File을 통한 로컬 저장소 이미지 저장을 사용합니다. Firebase는 구글 계정과 연동하여 사용자의 작성 데이터를 백업하기 위해서 사용하며, 네트워크가 연결되지 않는 상황 등을 대비하여 앱 사용 시에는 room과 로컬 저장소의 데이터를 활용하는 것입니다.


Location Search를 위해서는 Kakao Map API를 활용하며, 통신을 위해서는 Retrofit2 API를 사용했습니다. Map을 위한 API와 다른 것은 검색 기능 구현을 위해 참고할 수 있는 가장 좋은 글이 Kakao 제공 API를 사용하고 있었기 때문입니다. 앱이 필요로 하는 것은 검색 키워드에 따른 결과를 받아오는 것만 있기 때문에, 카테고리를 식당, 카페로 한 GET 요청을 사용하고 이에 대한 응답을 받기 위한 클래스들을 활용합니다.


각 Data Source를 받아오는 network, database 작업은 오랜 시간이 걸릴 수 있습니다. 특히 이미지 저장이나 아이템 복원 등은 생각보다 오랜 시간을 소모하기도 했습니다. 따라서 가능한 한 이러한 로직은 coroutine의 suspend 기능을 적극 활용하여 더 효율적인 비동기작업으로 수행되도록 조치했습니다. 
