-- Register an explicitly exploratory baseline for one human AChE IC50 activity.
INSERT INTO model_versions (name, version, activity_code, execution_type, training_data_description, metrics_json, is_active)
VALUES (
    'AChE SMILES n-gram logistic baseline',
    '0.1.0',
    'imppat_ic50_e662faf14ac2',
    'ML_BASELINE',
    'Locally trained from 74 clean, exact IC50 measurements in nM for human acetylcholinesterase (IMPPAT 3.0; 58 train, 16 random holdout). Five compounds with threshold-conflicting measurements were excluded. Labels use exploratory IC50 <= 10,000 nM threshold.',
    '{"testAccuracy":0.6875,"testBalancedAccuracy":0.6875,"testRocAuc":0.78125,"testBrier":0.23934876035639469,"testCompounds":16,"split":"stratified random molecule split; not scaffold split"}',
    TRUE
);
