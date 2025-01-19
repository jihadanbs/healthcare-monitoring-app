import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { FaUserInjured, FaCalendarAlt, FaNotesMedical, FaSignOutAlt } from 'react-icons/fa';
import PatientTable from './tables/PatientTable';
import AppointmentTable from './tables/AppointmentTable';
import MedicalRecordTable from './tables/MedicalRecordTable';

const DoctorDashboard = () => {
  const [activeView, setActiveView] = useState('overview'); 
  const [patients, setPatients] = useState([]);
  const [appointments, setAppointments] = useState([]);
  const [selectedPatient, setSelectedPatient] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      navigate('/login');
      return;
    }
    fetchDashboardData();
  }, [navigate]);

  const fetchDashboardData = async () => {
    try {
      const token = localStorage.getItem('token');
      const headers = {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      };
  
      // Fetch data patients dan appointments secara paralel
      const [patientsResponse, appointmentsResponse] = await Promise.all([
        fetch(`${process.env.REACT_APP_BASE_URL}/dashboard/doctor/patients`, { headers }),
        fetch(`${process.env.REACT_APP_BASE_URL}/dashboard/doctor/appointments`, { headers })
      ]);
  
      // Parsing JSON dari respons
      const patientsData = await patientsResponse.json();
      const appointmentsData = await appointmentsResponse.json();
  
      // Validasi data pasien
      if (patientsData.success) {
        setPatients(patientsData.patients);
      } else {
        console.error('Failed to fetch patients:', patientsData.message);
        setPatients([]); // Set kosong jika gagal
      }
  
      // Validasi data janji temu
      if (appointmentsData.success) {
        setAppointments(appointmentsData.appointments);
      } else {
        console.error('Failed to fetch appointments:', appointmentsData.message);
        setAppointments([]); // Set kosong jika gagal
      }
  
      // Hentikan loading
      setLoading(false);
    } catch (error) {
      console.error('Error fetching dashboard data:', error);
      setLoading(false);
    }
  };
  
  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/login');
  };

  const renderContent = () => {
    switch (activeView) {
      case 'patients':
        return (
          <div>
            <h2 className="text-2xl font-bold mb-4">Manajemen Pasien</h2>
            <PatientTable />
          </div>
        );
      case 'appointments':
        return (
          <div>
            <h2 className="text-2xl font-bold mb-4">Janji temu</h2>
            <AppointmentTable />
          </div>
        );
        case 'records':
          return (
            <div>
              <h2 className="text-2xl font-bold mb-4">Rekam Medis</h2>
              <MedicalRecordTable />
            </div>
          );
      default:
        return (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {/* Overview content */}
            {/* Recent Patients Card */}
            <div className="bg-white p-6 rounded-lg shadow-md">
              <h3 className="text-xl font-semibold mb-4">Pasien Terbaru</h3>
              <div className="space-y-4">
                {patients.slice(0, 5).map((patient) => (
                  <div
                    key={patient._id}
                    className="p-4 border rounded-lg cursor-pointer hover:bg-gray-50"
                    onClick={() => {
                      setSelectedPatient(patient);
                      setActiveView('records');
                    }}
                  >
                    <div className="font-medium">{patient.name}</div>
                    <div className="text-sm text-gray-600">{patient.email}</div>
                  </div>
                ))}
              </div>
            </div>

            {/* Upcoming Appointments Card */}
            <div className="bg-white p-6 rounded-lg shadow-md">
              <h3 className="text-xl font-semibold mb-4">Janji Temu Mendatang</h3>
              <div className="space-y-4">
                {appointments.slice(0, 5).map((appointment) => (
                  <div key={appointment._id} className="p-4 border rounded-lg">
                    <div className="font-medium">
                      {appointment.patient?.name || 'Unknown Patient'}
                    </div>
                    <div className="text-sm text-gray-600">
                      {new Date(appointment.date).toLocaleDateString()}
                    </div>
                    <div className="text-sm text-gray-600">
                      Status: {appointment.status}
                    </div>
                  </div>
                ))}
              </div>
            </div>

            {/* Quick Stats Card */}
            <div className="bg-white p-6 rounded-lg shadow-md">
              <h3 className="text-xl font-semibold mb-4">Statistik</h3>
              <div className="space-y-4">
                <div className="flex justify-between items-center p-4 bg-blue-50 rounded-lg">
                  <span>Total Pasien</span>
                  <span className="font-bold">{patients.length}</span>
                </div>
                <div className="flex justify-between items-center p-4 bg-green-50 rounded-lg">
                  <span>Janji Temu Mendatang</span>
                  <span className="font-bold">{appointments.length}</span>
                </div>
              </div>
            </div>
          </div>
        );
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-2xl text-gray-600">Loading...</div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-100">
      {/* Sidebar */}
      <div className="fixed left-0 top-0 h-full w-64 bg-indigo-800 text-white p-6">
        <h2 className="text-2xl font-bold mb-8">Portal Dokter</h2>
        <nav className="space-y-4">
          <button 
            onClick={() => setActiveView('overview')}
            className={`flex items-center space-x-3 w-full p-3 rounded ${
              activeView === 'overview' ? 'bg-indigo-700' : 'hover:bg-indigo-700'
            }`}
          >
            <span>Dashboard</span>
          </button>
          <button 
            onClick={() => setActiveView('patients')}
            className={`flex items-center space-x-3 w-full p-3 rounded ${
              activeView === 'patients' ? 'bg-indigo-700' : 'hover:bg-indigo-700'
            }`}
          >
            <FaUserInjured />
            <span>Pasien ({patients.length})</span>
          </button>
          <button 
            onClick={() => setActiveView('appointments')}
            className={`flex items-center space-x-3 w-full p-3 rounded ${
              activeView === 'appointments' ? 'bg-indigo-700' : 'hover:bg-indigo-700'
            }`}
          >
            <FaCalendarAlt />
            <span>Janji Temu ({appointments.length})</span>
          </button>
          <button 
            onClick={() => setActiveView('records')}
            className={`flex items-center space-x-3 w-full p-3 rounded ${
              activeView === 'records' ? 'bg-indigo-700' : 'hover:bg-indigo-700'
            }`}
          >
            <FaNotesMedical />
            <span>Rekam Medis</span>
          </button>
          <button 
            onClick={handleLogout}
            className="flex items-center space-x-3 w-full p-3 rounded hover:bg-indigo-700 mt-auto"
          >
            <FaSignOutAlt />
            <span>Logout</span>
          </button>
        </nav>
      </div>

      {/* Main Content */}
      <div className="ml-64 p-8">
        {renderContent()}
      </div>
    </div>
  );
};

export default DoctorDashboard;