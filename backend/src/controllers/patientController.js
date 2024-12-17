const Medicine = require('../models/Medicine');
const Appointment = require('../models/Appointment');
const HealthRecord = require('../models/HealthRecord');

exports.getDashboard = async (req, res) => {
    try {
        const patientId = req.user.id; // ID pasien dari token
        const medicines = await Medicine.find({ patientId });
        const appointments = await Appointment.find({ patientId });
        const healthRecords = await HealthRecord.find({ patientId });

        res.status(200).json({
            medicines,
            appointments,
            healthRecords
        });
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
};

exports.addMedicine = async (req, res) => {
    try {
        const { name } = req.body;
        const medicine = new Medicine({ name, patientId: req.user.id });
        await medicine.save();
        res.status(201).json({ message: 'Obat berhasil ditambahkan', medicine });
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
};

exports.scheduleAppointment = async (req, res) => {
    try {
        const { doctorId, date } = req.body;
        const appointment = new Appointment({ doctorId, date, patientId: req.user.id });
        await appointment.save();
        res.status(201).json({ message: 'Jadwal konsultasi berhasil dibuat', appointment });
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
};
