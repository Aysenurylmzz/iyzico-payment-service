import {
  BrowserRouter,
  Routes,
  Route,
} from "react-router-dom";

import PaymentPage from "./pages/PaymentPage";
import PaymentResultPage from "./pages/PaymentResultPage";

function App() {
  return (
    <BrowserRouter>
      <Routes>

        <Route
          path="/"
          element={<PaymentPage />}
        />

        <Route
          path="/payment/result"
          element={<PaymentResultPage />}
        />

      </Routes>
    </BrowserRouter>
  );
}

export default App;
