import React from 'react';
import { BrowserRouter as Router, Route, Routes, Link } from 'react-router-dom';
import Login from './components/Login';
import Register from './components/Register';

const App = () => {
  return (
    <Router>
      <div>
        <h1>Doctor Portal</h1>
        <nav>
          <Link to="/login">Login</Link> | 
          <Link to="/register">Register</Link>
        </nav>
        <Routes>
          <Route path="/" element={<Login />} />  {/* Default ke Login */}
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
        </Routes>
      </div>
    </Router>
  );
};

export default App;
