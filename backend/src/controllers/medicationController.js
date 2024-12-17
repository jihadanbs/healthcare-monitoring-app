const Medication = require('../models/Medicine');

exports.addMedication = async (req, res) => {
    try {
        const { name, description, price, prescribedBy } = req.body;
        const newMedication = new Medication({ name, description, price, prescribedBy });
        await newMedication.save();
        res.status(201).json(newMedication);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
};

exports.getMedications = async (req, res) => {
    try {
        const medications = await Medication.find().populate('prescribedBy', 'name');
        res.status(200).json(medications);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
};