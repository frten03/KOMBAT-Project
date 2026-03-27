/* =============================
   CORE GAME TYPES (Model)
============================= */

export type GamePhase = "SETUP" | "RUNNING" | "FINISHED"
export type GameMode = "DUEL" | "SOLITAIRE" | "AUTO"
export type PlayerNumber = 1 | 2

/* ---------- Minion Types ---------- */

export type MinionConfig = {
    type: string
    customName: string
    def: number
    strategy: string
    isValid: boolean
    avatar: any
}

export type Minion = {
    id: string
    row: number
    col: number
    hp: number
    maxHp: number
    defense: number
    typeName: string
    owner: number
}

export type MinionTypeDTO = {
    name: string
    defense: number
    strategy: string
}

/* ---------- Player State ---------- */

export type PlayerState = {
    budget: number
    spawnsLeft: number
    spawnableHexes: string[]
    minionTypes: MinionTypeDTO[];
}

/* ---------- Board Model ---------- */

export type HexCoord = {
    row: number
    col: number
    owner: number | null
    minion: Minion | null
}

/* ---------- Game State DTO (from backend) ---------- */

export type GameStateDTO = {
    phase: GamePhase
    currentPlayer: number
    globalTurn: number
    budget: number
    spawnsLeft: number
    player1Minions: Minion[]
    player2Minions: Minion[]
    player1SpawnableHexes: string[]
    player2SpawnableHexes: string[]
    availableMinionTypes: MinionTypeDTO[]
    spawnCost: number
    hexPurchaseCost: number
    gameOverReason: string | null
    gameOver: boolean
    turnLogs: string[]
    player1MinionTypes: MinionTypeDTO[];
    player2MinionTypes: MinionTypeDTO[];
}

/* ---------- Game Session DTO ---------- */

export type GameSessionDTO = {
    gameId: string
    mode: string
    started: boolean
    playerCount: number
}

/* ---------- Frontend Game State ---------- */

export type FrontendGameState = {
    gameId: string
    phase: GamePhase
    turn: number
    globalTurn: number
    currentPlayer: PlayerNumber
    currentPlayerSpawnsLeft: number
    mode: GameMode
    players: Record<PlayerNumber, PlayerState>
    board: HexCoord[]
    player1Minions: Minion[]
    player2Minions: Minion[]
    turnLogs: string[]
}