import React, { useState } from 'react';

const MedicalRecordForm = ({ patientId, onSubmit, onCancel }) => {
  const [formData, setFormData] = useState({
    diagnosis: '',
    symptoms: [''],
    prescription: [{ medicine: '', dosage: '', frequency: '', price: '' }],
    additionalNotes: '',
  });

  const handleSubmit = async (e) => {
    e.preventDefault();
    const token = localStorage.getItem('token');
    
    try {
      const response = await fetch(`${process.env.REACT_APP_BASE_URL}/dashboard/doctor/patients/${patientId}/medical-records`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          ...formData,
          prescription: formData.prescription.map(p => ({
            ...p,
            price: Number(p.price)
          }))
        })
      });

      const data = await response.json();
      if (data.success) {
        onSubmit(data.medicalRecord);
      }
    } catch (error) {
      console.error('Error creating medical record:', error);
    }
  };

  const addSymptom = () => {
    setFormData({
      ...formData,
      symptoms: [...formData.symptoms, '']
    });
  };

  const removeSymptom = (index) => {
    const newSymptoms = formData.symptoms.filter((_, i) => i !== index);
    setFormData({
      ...formData,
      symptoms: newSymptoms
    });
  };

  const addPrescription = () => {
    setFormData({
      ...formData,
      prescription: [...formData.prescription, { medicine: '', dosage: '', frequency: '', price: '' }]
    });
  };

  const removePrescription = (index) => {
    const newPrescription = formData.prescription.filter((_, i) => i !== index);
    setFormData({
      ...formData,
      prescription: newPrescription
    });
  };

  return (
    <div className="bg-white p-6 rounded-lg shadow-md w-full max-w-4xl mx-auto">
      <div className="mb-6">
        <h2 className="text-2xl font-bold">Tambah Rekam Medis</h2>
      </div>
      
      <form onSubmit={handleSubmit} className="space-y-6">
        {/* Diagnosis */}
        <div>
          <label className="block text-sm font-medium mb-2">Diagnosis</label>
          <input
            type="text"
            className="w-full p-2 border rounded"
            value={formData.diagnosis}
            onChange={(e) => setFormData({...formData, diagnosis: e.target.value})}
            required
          />
        </div>

        {/* Gejala */}
        <div>
          <label className="block text-sm font-medium mb-2">Gejala</label>
          {formData.symptoms.map((symptom, index) => (
            <div key={index} className="flex gap-2 mb-2">
              <input
                type="text"
                className="flex-1 p-2 border rounded"
                value={symptom}
                onChange={(e) => {
                  const newSymptoms = [...formData.symptoms];
                  newSymptoms[index] = e.target.value;
                  setFormData({...formData, symptoms: newSymptoms});
                }}
                required
              />
              {formData.symptoms.length > 1 && (
                <button
                  type="button"
                  onClick={() => removeSymptom(index)}
                  className="px-3 py-2 bg-red-500 text-white rounded hover:bg-red-600"
                >
                  -
                </button>
              )}
            </div>
          ))}
          <button
            type="button"
            onClick={addSymptom}
            className="mt-2 px-3 py-2 bg-green-500 text-white rounded hover:bg-green-600"
          >
            + Tambah Gejala
          </button>
        </div>

        {/* Resep Obat */}
        <div>
          <label className="block text-sm font-medium mb-2">Resep Obat</label>
          {formData.prescription.map((med, index) => (
            <div key={index} className="grid grid-cols-5 gap-2 mb-2">
              <input
                type="text"
                placeholder="Nama Obat"
                className="p-2 border rounded"
                value={med.medicine}
                onChange={(e) => {
                  const newPrescription = [...formData.prescription];
                  newPrescription[index] = {...med, medicine: e.target.value};
                  setFormData({...formData, prescription: newPrescription});
                }}
                required
              />
              <input
                type="text"
                placeholder="Dosis"
                className="p-2 border rounded"
                value={med.dosage}
                onChange={(e) => {
                  const newPrescription = [...formData.prescription];
                  newPrescription[index] = {...med, dosage: e.target.value};
                  setFormData({...formData, prescription: newPrescription});
                }}
                required
              />
              <input
                type="text"
                placeholder="Frekuensi"
                className="p-2 border rounded"
                value={med.frequency}
                onChange={(e) => {
                  const newPrescription = [...formData.prescription];
                  newPrescription[index] = {...med, frequency: e.target.value};
                  setFormData({...formData, prescription: newPrescription});
                }}
                required
              />
              <input
                type="number"
                placeholder="Harga"
                className="p-2 border rounded"
                value={med.price}
                onChange={(e) => {
                  const newPrescription = [...formData.prescription];
                  newPrescription[index] = {...med, price: e.target.value};
                  setFormData({...formData, prescription: newPrescription});
                }}
                required
              />
              {formData.prescription.length > 1 && (
                <button
                  type="button"
                  onClick={() => removePrescription(index)}
                  className="px-3 py-2 bg-red-500 text-white rounded hover:bg-red-600"
                >
                  -
                </button>
              )}
            </div>
          ))}
          <button
            type="button"
            onClick={addPrescription}
            className="mt-2 px-3 py-2 bg-green-500 text-white rounded hover:bg-green-600"
          >
            + Tambah Obat
          </button>
        </div>

        {/* Catatan Tambahan */}
        <div>
          <label className="block text-sm font-medium mb-2">Catatan Tambahan</label>
          <textarea
            className="w-full p-2 border rounded"
            rows="4"
            value={formData.additionalNotes}
            onChange={(e) => setFormData({...formData, additionalNotes: e.target.value})}
          />
        </div>

        {/* Tombol Submit */}
        <div className="flex justify-end gap-4">
          <button
            type="button"
            onClick={onCancel}
            className="px-4 py-2 bg-gray-500 text-white rounded hover:bg-gray-600"
          >
            Batal
          </button>
          <button
            type="submit"
            className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
          >
            Simpan Rekam Medis
          </button>
        </div>
      </form>
    </div>
  );
};

export default MedicalRecordForm;