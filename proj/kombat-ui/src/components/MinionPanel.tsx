import "../styles/minionpanel.css"

interface Minion {
    customName: string
    avatar: string
}

interface MinionPanelProps {
    minions?: Minion[]
}

export default function MinionPanel({ minions = [] }: MinionPanelProps) {
    return (
        <div className="minion-panel">

            {minions.map((minion, i) => (
                <div key={i} className="minion-slot">
                    <img
                        src={minion.avatar}
                        alt={minion.customName}
                        className="minion-avatar"
                    />
                    <div className="minion-name">
                        {minion.customName}
                    </div>
                </div>
            ))}

            <div className="spawn-cost">
                spawn cost: 100 🪙
            </div>

        </div>
    )
}