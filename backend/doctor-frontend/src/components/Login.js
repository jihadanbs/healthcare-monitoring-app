import React, { useState } from 'react';
import { FaUser, FaLock } from 'react-icons/fa';

const Login = () => {
  const [formData, setFormData] = useState({ email: '', password: '' });
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const response = await fetch(`${process.env.REACT_APP_BASE_URL}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData),
      });

      const data = await response.json();
      if (response.ok) {
        setMessage('Login berhasil!');
        localStorage.setItem('token', data.token);
      } else {
        setMessage(data.message || 'Login gagal');
      }
    } catch (error) {
      setMessage('Kesalahan server');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-indigo-500 to-red-600 p-6">
      <div className="bg-white p-10 rounded-3xl shadow-2xl w-full max-w-md transform hover:scale-105 transition-transform duration-300">
        <h2 className="text-4xl font-extrabold text-center mb-8 text-gray-900">Welcome Back</h2>
        <form onSubmit={handleSubmit} className="space-y-6" autoComplete='off'>
          <div className="relative">
            <FaUser className="absolute left-4 top-3 text-gray-400" />
            <input type="email" name="email" placeholder="Email Address" value={formData.email} onChange={handleChange} required className="pl-12 pr-4 py-3 border rounded-xl w-full focus:ring-2 focus:ring-indigo-400 focus:border-transparent hover:border-indigo-300 transition"/>
          </div>
          <div className="relative">
            <FaLock className="absolute left-4 top-3 text-gray-400" />
            <input type="password" name="password" placeholder="Password" value={formData.password} onChange={handleChange} required className="pl-12 pr-4 py-3 border rounded-xl w-full focus:ring-2 focus:ring-indigo-400 focus:border-transparent hover:border-indigo-300 transition"/>
          </div>
          <button type="submit" disabled={loading} className={`w-full py-3 rounded-xl text-white transition duration-300 ${loading ? 'bg-gray-400' : 'bg-indigo-600 hover:bg-indigo-700'}`}>
            {loading ? 'Loading...' : 'Login'}
          </button>
        </form>
        {message && (
          <p className={`mt-6 text-center ${message.includes('berhasil') ? 'text-green-600' : 'text-red-500'}`}>
            {message}
          </p>
        )}
        <div className="text-center mt-8">
          <a href="/register" className="text-indigo-500 hover:underline">
            Belum punya akun? Daftar disini
          </a>
        </div>
      </div>
    </div>
  );
};

export default Login;
