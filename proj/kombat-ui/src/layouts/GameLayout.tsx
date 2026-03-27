import "../styles/layout.css"
import bgImage from "../assets/background/Image.png"

interface Props {
    header: React.ReactNode
    board: React.ReactNode
    side: React.ReactNode
    log: React.ReactNode
}

export default function GameLayout({
                                       header,
                                       board,
                                       side,
                                       log
                                   }: Props) {
    return (
        <div className="game-container"
             style={{
                 backgroundImage: `url(${bgImage})`,
                 backgroundSize: "cover",
                 backgroundPosition: "center",
             }}>

            <div className="top-bar">
                {header}
            </div>

            <div className="game-body">

                <div className="left-spawn">
                    {log}
                </div>

                <div className="board-wrapper">
                    {board}
                </div>

                <div className="right-sidebar">
                    {side}
                </div>

            </div>
        </div>
    )
}