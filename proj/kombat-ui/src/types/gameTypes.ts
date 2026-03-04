/* =============================
   CORE GAME TYPES (Model)
============================= */

export type Player = "Player 1" | "Player 2"

export type GameMode = "Duel" | "Solitaire" | "Auto"

/* ---------- Minion Model ---------- */

export type MinionConfig = {
    id: string
    type: string
    customName: string
    def: number
    strategy: string
    isValid: boolean
}

/* ---------- Player State ---------- */

export type PlayerState = {
    budget: number
    spawnsLeft: number
}

/* ---------- Board Model ---------- */

export type Hex = {
    id: string
    owner: Player | null
    minionId: string | null
}

/* ---------- Game State ---------- */

export type GameState = {
    turn: number
    currentPlayer: Player
    mode: GameMode
    players: Record<Player, PlayerState>
    board: Hex[]
    minions: MinionConfig[]
    log: string[]
}