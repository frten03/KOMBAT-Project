import { create } from 'zustand'
import type {
    FrontendGameState,
    HexCoord,
    MinionTypeDTO,
    GameMode,
    GameStateDTO
} from '../types/gameTypes'

interface GameStore {
    gameId: string
    mode: GameMode
    
    gameState: FrontendGameState | null
    loading: boolean
    error: string | null
    
    availableMinionTypes: MinionTypeDTO[]
    currentPlayerBudget: number
    
    showErrorModal: boolean
    errorMessage: string
    
    showBuyConfirm: boolean
    showSpawnConfirm: boolean
    
    adjacentHexes: string[]
    selectedHex: HexCoord | null
    selectedMinionType: string | null
    
    isAutoPlaying: boolean
    hasBoughtHex: boolean
    hasSpawnedMinion: boolean
    
    gameOverReason: string | null
    winner: string | null
    
    setGameId: (id: string) => void
    setMode: (mode: GameMode) => void
    
    setGameState: (state: FrontendGameState) => void
    setLoading: (loading: boolean) => void
    setError: (error: string | null) => void
    
    setAvailableMinionTypes: (types: MinionTypeDTO[]) => void
    setCurrentPlayerBudget: (budget: number) => void
    
    showError: (message: string) => void
    closeErrorModal: () => void
    
    setShowBuyConfirm: (show: boolean) => void
    setShowSpawnConfirm: (show: boolean) => void
    
    setAdjacentHexes: (hexes: string[]) => void
    setSelectedHex: (hex: HexCoord | null) => void
    setSelectedMinionType: (type: string | null) => void
    
    setIsAutoPlaying: (playing: boolean) => void
    setHasBoughtHex: (bought: boolean) => void
    setHasSpawnedMinion: (spawned: boolean) => void
    
    setGameOver: (reason: string, winner: string) => void
    reset: () => void
}

const initialState = {
    gameId: '',
    mode: 'SOLITAIRE' as GameMode,
    gameState: null,
    loading: true,
    error: null,
    availableMinionTypes: [],
    currentPlayerBudget: 0,
    showErrorModal: false,
    errorMessage: '',
    showBuyConfirm: false,
    showSpawnConfirm: false,
    adjacentHexes: [],
    selectedHex: null,
    selectedMinionType: null,
    isAutoPlaying: false,
    hasBoughtHex: false,
    hasSpawnedMinion: false,
    gameOverReason: null,
    winner: null,
}

export const useGameStore = create<GameStore>((set) => ({
    ...initialState,
    
    setGameId: (id) => set({ gameId: id }),
    setMode: (mode) => set({ mode }),
    
    setGameState: (state) => set({ gameState: state }),
    setLoading: (loading) => set({ loading }),
    setError: (error) => set({ error }),
    
    setAvailableMinionTypes: (types) => set({ availableMinionTypes: types }),
    setCurrentPlayerBudget: (budget) => set({ currentPlayerBudget: budget }),
    
    showError: (message) => set({ errorMessage: message, showErrorModal: true }),
    closeErrorModal: () => set({ showErrorModal: false, errorMessage: '' }),
    
    setShowBuyConfirm: (show) => set({ showBuyConfirm: show }),
    setShowSpawnConfirm: (show) => set({ showSpawnConfirm: show }),
    
    setAdjacentHexes: (hexes) => set({ adjacentHexes: hexes }),
    setSelectedHex: (hex) => set({ selectedHex: hex }),
    setSelectedMinionType: (type) => set({ selectedMinionType: type }),
    
    setIsAutoPlaying: (playing) => set({ isAutoPlaying: playing }),
    setHasBoughtHex: (bought) => set({ hasBoughtHex: bought }),
    setHasSpawnedMinion: (spawned) => set({ hasSpawnedMinion: spawned }),
    
    setGameOver: (reason, winner) => set({ gameOverReason: reason, winner }),
    
    reset: () => set(initialState),
}))

export function dtoToFrontend(dto: GameStateDTO, mode: GameMode): FrontendGameState {
    const currentPlayer = dto.currentPlayer as 1 | 2
    const board: HexCoord[] = []

    const allMinions = [...(dto.player1Minions || []), ...(dto.player2Minions || [])]

    for (let r = 0; r < 8; r++) {
        for (let c = 0; c < 8; c++) {
            const minion = allMinions.find(m => m.row === r && m.col === c) || null

            let owner: number | null = null
            const key = `${c},${r}`

            if (dto.player1SpawnableHexes?.includes(key)) {
                owner = 1
            } else if (dto.player2SpawnableHexes?.includes(key)) {
                owner = 2
            }

            board.push({ row: r, col: c, owner, minion })
        }
    }

    return {
        gameId: "",
        phase: dto.phase,
        turn: dto.globalTurn,
        globalTurn: dto.globalTurn,
        currentPlayer,
        currentPlayerSpawnsLeft: dto.spawnsLeft,
        mode,
        players: {
            1: {
                budget: dto.currentPlayer === 1 ? dto.budget : 0,
                spawnsLeft: dto.currentPlayer === 1 ? dto.spawnsLeft : 0,
                spawnableHexes: dto.player1SpawnableHexes || [],
                minionTypes: dto.player1MinionTypes || []
            },
            2: {
                budget: dto.currentPlayer === 2 ? dto.budget : 0,
                spawnsLeft: dto.currentPlayer === 2 ? dto.spawnsLeft : 0,
                spawnableHexes: dto.player2SpawnableHexes || [],
                minionTypes: dto.player2MinionTypes || []
            }
        },
        board,
        player1Minions: dto.player1Minions || [],
        player2Minions: dto.player2Minions || [],
        turnLogs: dto.turnLogs ?? []
    }
}

export function getAdjacentHexes(spawnableHexes: string[]): string[] {
    const spawnableSet = new Set(spawnableHexes);
    const adjacent = new Set<string>();

    for (const hex of spawnableHexes) {
        const [c, r] = hex.split(",").map(Number);

        let dc: number[], dr: number[];

        // Even-Q: Shove EVEN columns DOWN
        if (c % 2 === 0) {
            dc = [0, 0, -1, -1, +1, +1];
            dr = [-1, +1, 0, 1, 0, 1];
        } else {
            dc = [0, 0, -1, -1, +1, +1];
            dr = [-1, +1, -1, 0, -1, 0];
        }

        for (let i = 0; i < 6; i++) {
            const nc = c + dc[i];
            const nr = r + dr[i];
            const key = `${nc},${nr}`;

            if (nc >= 0 && nc < 8 && nr >= 0 && nr < 8 && !spawnableSet.has(key)) {
                adjacent.add(key);
            }
        }
    }
    return Array.from(adjacent);
}