```mermaid
stateDiagram-v2
    [*] --> MENU
    MENU --> PLAYING
    MENU --> LEADERBOARD
    PLAYING --> GAME_OVER
    PLAYING --> MENU
    GAME_OVER --> PLAYING
    GAME_OVER --> MENU
    GAME_OVER --> LEADERBOARD
    LEADERBOARD --> MENU
```
