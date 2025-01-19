import React, { useState, useEffect } from 'react';
import { FaCheck, FaTimes } from 'react-icons/fa';

const AppointmentTable = () => {
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchAppointments();
  }, []);

  const fetchAppointments = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`${process.env.REACT_APP_BASE_URL}/doctor/appointments`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      const data = await response.json();
      setAppointments(data.appointments);
      setLoading(false);
    } catch (error) {
      setError('Failed to fetch appointments');
      setLoading(false);
    }
  };

  if (loading) return <div>Loading...</div>;
  if (error) return <div>{error}</div>;

  return (
    <div className="overflow-x-auto">
      <table className="min-w-full bg-white rounded-lg overflow-hidden">
        <thead className="bg-gray-100">
          <tr>
            <th className="px-6 py-3 border-b text-left">Patient</th>
            <th className="px-6 py-3 border-b text-left">Date</th>
            <th className="px-6 py-3 border-b text-left">Time</th>
            <th className="px-6 py-3 border-b text-left">Status</th>
            <th className="px-6 py-3 border-b text-center">Actions</th>
          </tr>
        </thead>
        <tbody>
          {appointments.map((appointment) => (
            <tr key={appointment._id} className="hover:bg-gray-50">
              <td className="px-6 py-4 border-b">{appointment.patient?.name || 'Unknown Patient'}</td>
              <td className="px-6 py-4 border-b">
                {new Date(appointment.date).toLocaleDateString()}
              </td>
              <td className="px-6 py-4 border-b">
                {new Date(appointment.date).toLocaleTimeString()}
              </td>
              <td className="px-6 py-4 border-b">{appointment.status}</td>
              <td className="px-6 py-4 border-b">
                <div className="flex justify-center space-x-2">
                  <button
                    className="p-2 bg-green-500 text-white rounded hover:bg-green-600"
                    title="Confirm Appointment"
                  >
                    <FaCheck />
                  </button>
                  <button
                    className="p-2 bg-red-500 text-white rounded hover:bg-red-600"
                    title="Cancel Appointment"
                  >
                    <FaTimes />
                  </button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default AppointmentTable;