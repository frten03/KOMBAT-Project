import type { GameState } from "../types/gameTypes"


const rows = 8
const cols = 8

const mockBoard = Array.from({ length: rows * cols }).map((_, i) => {
    const r = Math.floor(i / cols)
    const c = i % cols

    let owner: "Player 1" | "Player 2" | null = null

    /* 🔵 มุมซ้ายบน 5 ช่อง */
    if (
        (r === 0 && c < 3) ||      // แถวบนสุด 3 ช่อง
        (r === 1 && c < 2)         // แถวถัดมา 2 ช่อง
    ) {
        owner = "Player 1"
    }

    /* 🔴 มุมขวาล่าง 5 ช่อง */
    if (
        (r === rows - 1 && c >= cols - 3) ||   // แถวล่างสุด 3 ช่อง
        (r === rows - 2 && c >= cols - 2)      // แถวบนล่างสุด 2 ช่อง
    ) {
        owner = "Player 2"
    }

    return {
        id: `hex-${i}`,
        owner,
        minionId: null
    }
})

const mockMinions = [
    {
        id: "m1",
        type: "Tank",
        customName: "Iron Wall",
        def: 150,
        strategy: "Defensive",
        isValid: true
    },
    {
        id: "m2",
        type: "Archer",
        customName: "Swift Arrow",
        def: 80,
        strategy: "Ranged",
        isValid: true
    }
]

export const mockGameState: GameState = {
    turn: 1,
    currentPlayer: "Player 1",
    mode: "Duel",

    players: {
        "Player 1": {
            budget: 5000,
            spawnsLeft: 30
        },
        "Player 2": {
            budget: 5000,
            spawnsLeft: 30
        }
    },

    board: mockBoard,

    minions: mockMinions,

    log: [
        "Game started.",
        "Player 1 turn begins."
    ]
}