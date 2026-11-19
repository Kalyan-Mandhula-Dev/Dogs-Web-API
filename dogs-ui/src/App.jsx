import './App.css'
import { BrowserRouter as Router, Routes, Route } from 'react-router'
import Breeds from './components/breeds/Breeds'
import SubBreeds from './components/breeds/SubBreeds'

function App() {

  return (
    <>
      <Router>
        <Routes>
          <Route path="/" element={<Breeds />} />
          <Route path="/breeds/:breedId/:breedName" element={<SubBreeds />} />
        </Routes>
      </Router>
    </>
  )
}

export default App
