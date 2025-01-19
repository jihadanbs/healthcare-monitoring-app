import React, { useState, useEffect } from 'react';
import { FaTrash } from 'react-icons/fa';

const MedicalRecordTable = () => {
  const [records, setRecords] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const token = localStorage.getItem('token');
    console.log('Token exists:', !!token);
    fetchMedicalRecords();
  }, []);

  useEffect(() => {
    console.log('Records state updated:', records);
  }, [records]);

  const fetchMedicalRecords = async () => {
    try {
      const token = localStorage.getItem('token');
      console.log('Token yang akan dikirim:', token);
      
      const response = await fetch(`${process.env.REACT_APP_BASE_URL}/dashboard/doctor/medical-records`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      
      console.log('Status response:', response.status);
      const data = await response.json();
      console.log('Data yang diterima:', data);
      
      if (response.ok) {
        setRecords(data.medicalRecords);
        setError(null);
      } else {
        setError(data.message || 'Gagal mengambil medical records');
        console.error('Error response:', data);
      }
    } catch (error) {
      console.error('Error fetch:', error);
      setError('Gagal mengambil medical records');
    } finally {
      setLoading(false); 
    }
  };

  const handleDelete = async (recordId, patientId) => {
    if (window.confirm('Apakah anda yakin ingin menghapus data ?')) {
      try {
        const token = localStorage.getItem('token');
        const response = await fetch(
          `${process.env.REACT_APP_BASE_URL}/dashboard/doctor/patients/${patientId}/medical-records/${recordId}`,
          {
            method: 'DELETE',
            headers: {
              'Authorization': `Bearer ${token}`
            }
          }
        );
        
        if (response.ok) {
          fetchMedicalRecords();
        } else {
          const data = await response.json();
          setError(data.message || 'Failed to delete record');
        }
      } catch (error) {
        setError('Failed to delete record');
      }
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-[200px]">
        <div className="text-gray-600">
          <p>Loading...</p>
          <p className="text-sm">Mohon tunggu sebentar</p>
        </div>
      </div>
    );
  }
  
  console.log('Rendering table with records:', records); 
  return (
    <div className="space-y-4">
      <h2 className="text-xl font-bold mb-4">Riwayat Rekam Medis</h2>
      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
          {error}
        </div>
      )}
      
      {records.length === 0 ? (
        <div className="bg-gray-50 rounded-lg p-6 text-center text-gray-600">
         Tidak ditemukan catatan medis, catatan medis baru akan muncul di sini saat dibuat !
        </div>
      ) : (
        <div className="overflow-x-auto">
          <table className="min-w-full bg-white rounded-lg overflow-hidden">
            <thead className="bg-gray-100">
              <tr>
                <th className="px-6 py-3 border-b text-left">Tanggal</th>
                <th className="px-6 py-3 border-b text-left">Nama Pasien</th>
                <th className="px-6 py-3 border-b text-left">Diagnosis</th>
                <th className="px-6 py-3 border-b text-left">Gejala</th>
                <th className="px-6 py-3 border-b text-left">Resep</th>
                <th className="px-6 py-3 border-b text-left">Catatan</th>
                <th className="px-6 py-3 border-b text-center">Aksi</th>
              </tr>
            </thead>
            <tbody>
              {records.map((record) => (
                <tr key={record._id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 border-b">
                    {new Date(record.createdAt).toLocaleDateString()}
                  </td>
                  <td className="px-6 py-4 border-b font-medium">
                    {record.patient?.name || 'Unknown Patient'}
                  </td>
                  <td className="px-6 py-4 border-b">{record.diagnosis}</td>
                  <td className="px-6 py-4 border-b">
                    <ul className="list-disc list-inside">
                      {Array.isArray(record.symptoms) && record.symptoms.map((symptom, index) => (
                        <li key={index}>{symptom}</li>
                      ))}
                    </ul>
                  </td>
                  <td className="px-6 py-4 border-b">
                    <ul className="list-disc list-inside">
                      {Array.isArray(record.prescription) && record.prescription.map((med, index) => (
                        <li key={index}>
                          {med.medicine} - {med.dosage} ({med.frequency})
                          <br />
                          <span className="text-sm text-gray-600">
                            Price: Rp {med.price?.toLocaleString()}
                          </span>
                        </li>
                      ))}
                    </ul>
                  </td>
                  <td className="px-6 py-4 border-b">{record.additionalNotes}</td>
                  <td className="px-6 py-4 border-b">
                    <div className="flex justify-center space-x-2">
                      <button
                        onClick={() => handleDelete(record._id, record.patient._id)}
                        className="p-2 bg-red-500 text-white rounded hover:bg-red-600"
                        title="Delete Record"
                      >
                        <FaTrash />
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

export default MedicalRecordTable;