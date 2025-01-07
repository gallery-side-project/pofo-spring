# POFO
![GitHub Release](https://img.shields.io/github/v/release/team-pofo/pofo-spring)

토이프로젝트 검색 서비스

## Backend Team

<table>
    <tr>
        <td align="center">
            <a href="https://github.com/mclub4">조현진</a>
        </td>
        <td align="center">
            <a href="https://github.com/sukjuhong">이진우</a>
        </td>
    </tr>
    <tr>
        <td>
            <a href="https://github.com/mclub4"><img height="200px" width="200px" src="https://avatars.githubusercontent.com/u/55117706?v=4" alt="mclub4 avatar"/></a>
        </td>
        <td>
            <a href="https://github.com/sukjuhong"><img height="200px" width="200px" src="https://avatars.githubusercontent.com/u/102505374?v=4" alt="mclub4 avatar"/></a>
        </td>
    </tr>
</table>

## 🌳 Branch Convention

- main
    - 배포 가능한 상태의 코드만을 관리하는 프로덕션용 브랜치
- dev
    - 개발 전용 브랜치
    - 테스트 코드 통과 및 한 명 이상의 팀원의 승인 후 병합 가능
    - 개발 서버와 동기화
- 이슈 기반 브랜치
    - `feat/{브랜치명}`: 신규 기능 개발 시 브랜치명
    - `fix/{브랜치명}`: 급하지 않은 버그 조치 시 브랜치명
    - `hotfix/{브랜치명}`: 빠르게 수정해야 하는 버그 조치 시 브랜치명
    - `refactor/{브랜치명}`: 코드 개선 시 브랜치명

## Architecture

### Multi-module Architecture

![pofo-multi-module-architecture](https://github.com/user-attachments/assets/8f28a733-5ac9-491e-88e1-4a7220623f2f)

- `pofo-api`: 프론트엔드 측에서 사용하는 API module
- `pofo-infra`: Executable Module에서 공통으로 사용하는 외부 의존성 module
- `pofo-domain`: 도메인 객체와 Database 접근을 담당하는 module
- `pofo-common`: 의존성이 없으며, 특정 도메인에 종속되지 않는 공통 모듈

### Infrastructure Architecture

![pofo-infrastructure-architecture](https://github.com/user-attachments/assets/350ae65b-39d0-48b9-a736-967ab8a86b42)

