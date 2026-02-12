# 📺 Streaming Service Project

사용자 맞춤형 동영상 스트리밍 및 채널 관리 플랫폼입니다. Spring Boot 3.x를 기반으로 고효율 비디오 인코딩(AV1), 실시간 상태 알림(SSE), 그리고 JWT 보안 인증을 결합하여 견고한 스트리밍 환경을 구축했습니다.

---

## 🏗 Service Architecture (서비스 아키텍처)

본 프로젝트는 대용량 미디어 처리와 실시간 피드백을 위해 다음과 같은 비동기 이벤트 기반 구조로 설계되었습니다.



```mermaid
graph TD
    subgraph "Client Side"
        A[Web Browser]
    end

    subgraph "Spring Boot Server"
        B[API Controller]
        C[Spring Security / JWT]
        D[Async Video Service]
        E[SSE Session Manager]
    end

    subgraph "External Tools & Storage"
        F[FFmpeg / FFprobe]
        G[Local/Cloud Storage]
    end

    subgraph "Database"
        H[(MySQL)]
    end

    A -- "1. Video Upload (Multipart)" --> B
    B -- "2. Auth Check" --> C
    B -- "3. Start Async Process" --> D
    D -- "4. Progress Tracking" --> E
    E -- "5. Real-time Status (%)" --> A
    D -- "6. Encode & Probe" --> F
    F -- "7. Save Media Files" --> G
    D -- "8. Metadata Save" --> H
