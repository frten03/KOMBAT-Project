import { HashRouter, Routes, Route } from "react-router-dom"
import StartPage from "../pages/StartPage"
import RoomPage from "../pages/RoomPage"
import MinionSetupPage from "../pages/MinionSetupPage"
import JoinRoomPage from "../pages/JoinRoomPage"
import DuelWaitingPage from "../pages/DuelWaitingPage"
import AgreementPage from "../pages/AgreementPage.tsx";
import GameplayPage from "../pages/GameplayPage.tsx";
import EndGamePage from "../pages/EndGamePage.tsx";

export default function AppRouter() {
    return (
        <HashRouter>
            <Routes>
                <Route path="/" element={<StartPage />} />
                <Route path="/room" element={<RoomPage />} />
                <Route path="/waiting" element={<DuelWaitingPage />} />
                <Route path="/setup" element={<MinionSetupPage />} />
                <Route path="/join" element={<JoinRoomPage />} />
                <Route path="/agreement" element={<AgreementPage />} />
                <Route path="/gameplay" element={<GameplayPage />} />
                <Route path="/end" element={<EndGamePage />} />
            </Routes>
        </HashRouter>
    )
}