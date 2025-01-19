import React, { useState, useEffect } from 'react';
import { FaEdit, FaTrash, FaFileMedical, FaCalendarPlus } from 'react-icons/fa';
import MedicalRecordForm from '../forms/MedicalRecordForm';

const PatientTable = () => {
  const [patients, setPatients] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [selectedPatient, setSelectedPatient] = useState(null);

  useEffect(() => {
    fetchPatients();
  }, []);

  const fetchPatients = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`${process.env.REACT_APP_BASE_URL}/dashboard/doctor/patients`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      const data = await response.json();

      if (data.success) {
        setPatients(data.patients);
      } else {
        setError(data.message);
      }
      setLoading(false);
    } catch (error) {
      setError('Failed to fetch patients');
      setLoading(false);
    }
  };

  const handleAddMedicalRecord = (patient) => {
    setSelectedPatient(patient);
    setShowForm(true);
  };

  const handleFormSubmit = async (newRecord) => {
    // Refresh data setelah menambah record
    await fetchPatients();
    setShowForm(false);
    setSelectedPatient(null);
  };

  if (loading) return <div>Loading...</div>;
  if (error) return <div>{error}</div>;

  if (showForm) {
    return (
      <div className="space-y-4">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-xl font-bold">Tambahkan Rekam Medis untuk {selectedPatient.name}</h2>
          <button
            onClick={() => setShowForm(false)}
            className="px-4 py-2 bg-gray-500 text-white rounded hover:bg-gray-600"
          >
            Kembali ke daftar
          </button>
        </div>
        <MedicalRecordForm
          patientId={selectedPatient._id}
          onSubmit={handleFormSubmit}
          onCancel={() => {
            setShowForm(false);
            setSelectedPatient(null);
          }}
        />
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {loading && (
        <div className="flex justify-center items-center min-h-[200px]">
          <div className="text-gray-600">Loading...</div>
        </div>
      )}
      
      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
          {error}
        </div>  
      )}
   
      {!loading && !error && patients.length === 0 ? (
        <div className="bg-gray-50 rounded-lg p-6 text-center text-gray-600">
          Tidak ditemukan pasien, pasien baru akan muncul di sini saat telah registrasi !
        </div>
      ) : (
    <div className="overflow-x-auto">
      <table className="min-w-full bg-white rounded-lg overflow-hidden">
        <thead className="bg-gray-100">
          <tr>
            <th className="px-6 py-3 border-b text-left">Nama</th>
            <th className="px-6 py-3 border-b text-left">Email</th>
            <th className="px-6 py-3 border-b text-left">Profile</th>
            <th className="px-6 py-3 border-b text-center">Aksi</th>
          </tr>
        </thead>
        <tbody>
          {patients.map((patient) => (
            <tr key={patient._id} className="hover:bg-gray-50">
              <td className="px-6 py-4 border-b">{patient.name}</td>
              <td className="px-6 py-4 border-b">{patient.email}</td>
              <td className="px-6 py-4 border-b">
                {patient.profile ? (
                  <>
                    <div>Age: {patient.profile.age}</div>
                    <div>Gender: {patient.profile.gender}</div>
                  </>
                ) : (
                  'No profile data'
                )}
              </td>
              <td className="px-6 py-4 border-b">
                <div className="flex justify-center space-x-2">
                  <button
                    onClick={() => handleAddMedicalRecord(patient)}
                    className="p-2 bg-blue-500 text-white rounded hover:bg-blue-600"
                    title="Add Medical Record"
                  >
                    <FaFileMedical />
                  </button>
                  <button
                    className="p-2 bg-green-500 text-white rounded hover:bg-green-600"
                    title="Schedule Appointment"
                  >
                    <FaCalendarPlus />
                  </button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
     )}
 </div>
  );
};

export default PatientTable;