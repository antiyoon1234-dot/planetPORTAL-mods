# PlanetEarth Portal 거래 모드

Minecraft Java Edition **1.20.1 Fabric 클라이언트 모드**입니다. 게임 안에서 PlanetEarth Portal 거래글을 검색하고, Discord 계정을 연결한 뒤 판매·구매글을 등록할 수 있습니다.

> 이 모드는 서버 플러그인이 아닙니다. 거래 확정, 아이템 전달, 게임 내 결제는 반드시 게임 안에서 직접 확인하세요.

## 기능

- `I` 키로 거래소 열기
- 판매글·구매글 검색
- `금`, `금괴`, `금 블럭`, `gold`처럼 아이템 별칭 검색
- 포털에 등록되지 않은 아이템도 직접 입력해 등록
- Discord OAuth로 Minecraft 계정과 포털 계정 연결
- Minecraft 계정별 연결 토큰 저장 및 재실행 후 복원
- 웹 포털과 게임 양쪽에서 거래글 확인

## 설치 안내

### 필요한 것

- Minecraft Java Edition 1.20.1
- Fabric Loader 0.15.x 이상
- Fabric API 0.92.x for Minecraft 1.20.1

### 설치 순서

1. [Fabric 공식 설치기](https://fabricmc.net/use/installer/)로 Minecraft 1.20.1 Fabric 프로필을 설치합니다.
2. [Fabric API 1.20.1](https://modrinth.com/mod/fabric-api/versions?g=1.20.1)을 다운로드합니다.
3. 이 저장소의 [Releases](https://github.com/antiyoon1234-dot/planetPORTAL-mods/releases)에서 `planetearth-market-버전.jar`를 다운로드합니다.
4. 두 JAR 파일을 Minecraft `mods` 폴더에 넣습니다.

Windows 기본 경로:

```text
%APPDATA%\\.minecraft\\mods
```

5. Minecraft 런처에서 Fabric 1.20.1 프로필로 실행합니다.

## 사용자 메뉴얼

### 거래소 열기

게임에 접속한 뒤 `I` 키를 누릅니다. 키가 작동하지 않으면 `설정 → 조작 → 키 설정`에서 `PlanetEarth Market` 항목을 확인하세요.

### 거래글 검색

거래소 화면의 검색창에 아이템 이름을 입력합니다. 판매글과 구매글을 구분해 볼 수 있으며, `금 블럭`, `gold block`처럼 비슷한 표현도 검색됩니다.

### Discord 계정 연결

1. 거래소에서 `거래 등록`을 누릅니다.
2. `포털 열기`를 눌러 브라우저를 엽니다.
3. PlanetEarth Portal에서 Discord 로그인을 완료합니다.
4. 연결 완료 화면이 나올 때까지 Minecraft 화면을 닫지 않습니다.
5. 연결이 끝나면 거래 등록 화면으로 자동 이동합니다.

연결 토큰은 Minecraft 계정별 로컬 설정 파일에 저장됩니다. 토큰이 만료되거나 관리자에 의해 연동이 해제되면 새 연결 코드를 발급해야 합니다.

### 판매·구매글 등록

1. 아이템 이름, 수량, 개당 가격을 입력합니다.
2. `판매` 또는 `구매`를 선택합니다.
3. 필요한 경우 거래 조건을 입력합니다.
4. `등록`을 누릅니다.

등록된 글은 웹 포털의 거래 게시판에서도 확인할 수 있습니다.

## 문제 해결

### `Discord 계정을 연결해야 등록할 수 있습니다.`

모드에서 거래 등록을 누른 뒤 포털 Discord 로그인을 완료하지 않은 상태입니다. `거래소 → 거래 등록`에서 새 연결 코드를 발급해 다시 연결하세요.

### `연결 코드 발급 실패`

인터넷 연결과 포털 접속 상태를 확인한 뒤 Minecraft를 재실행하고 다시 시도하세요. 연결 코드는 제한 시간 안에 사용해야 합니다.

### 거래글이 보이지 않음

거래글은 공개 상태인 항목만 표시됩니다. 서버·포털 점검 중이거나 인터넷 연결이 불안정하면 잠시 후 거래소를 다시 여세요.

### Fabric에서 모드가 로드되지 않음

Minecraft 버전이 정확히 1.20.1인지, Fabric 프로필로 실행했는지, Fabric API가 `mods` 폴더에 함께 있는지 확인하세요.

## 개인정보 및 보안

- 모드에는 Supabase 공개 anon 키만 포함되며, 서버 비밀키나 Discord client secret은 포함하지 않습니다.
- Discord 비밀번호와 OAuth 인증은 PlanetEarth Portal과 Discord 공식 로그인 화면에서만 입력하세요.
- 연결 토큰이 포함된 로그나 설정 파일을 다른 사람에게 공유하지 마세요.
- 실제 아이템 이동과 거래 완료 여부는 게임 안에서 직접 확인하세요.

## 개발자 빌드

Java 17이 필요합니다.

```powershell
./gradlew build --no-daemon
```

빌드 결과물은 `build/libs/`에 생성됩니다. GitHub Actions는 `v*` 태그가 푸시되면 자동으로 빌드하고 GitHub Release에 JAR를 첨부합니다.

## 라이선스

이 프로젝트는 [MIT License](LICENSE)로 배포됩니다.
