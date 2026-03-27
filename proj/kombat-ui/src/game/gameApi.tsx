import type { GameSessionDTO, GameStateDTO } from "../types/gameTypes"

const API_BASE = "http://localhost:8080/game"
const STRATEGY_API_BASE = "http://localhost:8080/strategy"

export interface ValidationResult {
    valid: boolean
    message: string
}

async function fetchJson<T>(url: string, options?: RequestInit): Promise<T> {
    const response = await fetch(url, {
        ...options,
        headers: {
            "Content-Type": "application/json",
            ...options?.headers,
        },
    })
    
    if (!response.ok) {
        const error = await response.text()
        throw new Error(error || `HTTP ${response.status}`)
    }
    
    return response.json()
}

export interface ApiResponse<T> {
    success: boolean
    data?: T
    error?: string
}

export async function createGame(mode: string): Promise<GameSessionDTO> {
    return fetchJson<GameSessionDTO>(`${API_BASE}/create`, {
        method: "POST",
        body: JSON.stringify({ mode: mode.toLowerCase() }),
    })
}

export async function joinGame(gameId: string): Promise<GameSessionDTO> {
    console.log("[API] joinGame called with gameId:", gameId)
    return fetchJson<GameSessionDTO>(`${API_BASE}/join`, {
        method: "POST",
        body: JSON.stringify({ gameId }),
    })
}

export async function getGameState(gameId: string): Promise<GameStateDTO> {
    return fetchJson<GameStateDTO>(`${API_BASE}/${gameId}/state`)
}

export async function getSession(gameId: string): Promise<GameSessionDTO> {
    return fetchJson<GameSessionDTO>(`${API_BASE}/${gameId}`)
}

export async function selectTypes(
    gameId: string,
    types: string[]
): Promise<GameStateDTO> {
    return fetchJson<GameStateDTO>(`${API_BASE}/${gameId}/select-types`, {
        method: "POST",
        body: JSON.stringify({ types }),
    })
}

export async function initialSpawn(
    gameId: string,
    typeName: string,
    row: number,
    col: number
): Promise<GameStateDTO> {
    return fetchJson<GameStateDTO>(`${API_BASE}/${gameId}/initial-spawn`, {
        method: "POST",
        body: JSON.stringify({ typeName, row, col }),
    })
}

export async function spawnMinion(
    gameId: string,
    typeName: string,
    row: number,
    col: number
): Promise<GameStateDTO> {
    return fetchJson<GameStateDTO>(`${API_BASE}/${gameId}/spawn`, {
        method: "POST",
        body: JSON.stringify({ typeName, row, col }),
    })
}

export async function purchaseHex(
    gameId: string,
    row: number,
    col: number
): Promise<GameStateDTO> {
    return fetchJson<GameStateDTO>(`${API_BASE}/${gameId}/purchase`, {
        method: "POST",
        body: JSON.stringify({ row, col }),
    })
}

export async function nextTurn(gameId: string): Promise<GameStateDTO> {
    return fetchJson<GameStateDTO>(`${API_BASE}/${gameId}/next-turn`, {
        method: "POST",
    })
}

export async function surrender(gameId: string): Promise<GameStateDTO> {
    return fetchJson<GameStateDTO>(`${API_BASE}/${gameId}/surrender`, {
        method: "POST",
    })
}

export async function setupDone(gameId: string): Promise<GameStateDTO> {
    return fetchJson<GameStateDTO>(`${API_BASE}/${gameId}/setup-done`, {
        method: "POST",
    })
}

export async function buyHexApi(gameId: string, row: number, col: number) {
    try {
        const gameState = await purchaseHex(gameId, row, col)
        return { success: true, gameState }
    } catch (error) {
        return { success: false, gameState: null, error: String(error) }
    }
}

export async function placeMinionApi(
    gameId: string,
    row: number,
    col: number,
    minionType: string
) {
    try {
        const gameState = await spawnMinion(gameId, minionType, row, col)
        return { success: true, gameState }
    } catch (error) {
        return { success: false, gameState: null, error: String(error) }
    }
}

export async function validateStrategy(strategy: string): Promise<ValidationResult> {
    return fetchJson<ValidationResult>(`${STRATEGY_API_BASE}/validate`, {
        method: "POST",
        body: JSON.stringify({ strategy }),
    })
}

export async function startAutoPlay(gameId: string): Promise<GameStateDTO> {
    return fetchJson<GameStateDTO>(`${API_BASE}/${gameId}/auto-play/start`, {
        method: "POST",
    })
}

export async function stopAutoPlay(gameId: string): Promise<GameStateDTO> {
    return fetchJson<GameStateDTO>(`${API_BASE}/${gameId}/auto-play/stop`, {
        method: "POST",
    })
}

export async function isAutoPlaying(gameId: string): Promise<boolean> {
    return fetchJson<boolean>(`${API_BASE}/${gameId}/auto-play/status`)
}

export interface GameConfig {
    spawnCost: number
    hexPurchaseCost: number
    initBudget: number
    initHp: number
    turnBudget: number
    maxBudget: number
    interestPct: number
    maxTurns: number
    maxSpawns: number
}

export async function getGameConfig(): Promise<GameConfig> {
    return fetchJson<GameConfig>(`${API_BASE}/config`)
}

export interface RoomStatusDTO {
    guestJoined: boolean
    configSubmitted: boolean
    hostAgreed: boolean
    guestAgreed: boolean
    hostConfig: MinionTypeDTO[] | null
}

export interface MinionTypeDTO {
    name: string
    defense: number
    strategy: string
}

export async function getRoomStatus(gameId: string): Promise<RoomStatusDTO> {
    return fetchJson<RoomStatusDTO>(`${API_BASE}/${gameId}/room-status`)
}

export async function submitConfig(gameId: string, types: string[]): Promise<RoomStatusDTO> {
    return fetchJson<RoomStatusDTO>(`${API_BASE}/${gameId}/submit-config`, {
        method: "POST",
        body: JSON.stringify({ types }),
    })
}

export async function agree(gameId: string, role: string = "guest"): Promise<RoomStatusDTO> {
    return fetchJson<RoomStatusDTO>(`${API_BASE}/${gameId}/agree?role=${role}`, {
        method: "POST",
    })
}

export async function disagree(gameId: string): Promise<RoomStatusDTO> {
    return fetchJson<RoomStatusDTO>(`${API_BASE}/${gameId}/disagree`, {
        method: "POST",
    })
}