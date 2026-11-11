UPDATE model_versions
SET metrics_json = '{"testAccuracy":0.6875,"majorityBaselineAccuracy":0.5,"testBalancedAccuracy":0.6875,"testRocAuc":0.78125,"majorityBaselineRocAuc":0.5,"testPrAuc":0.82192460317460314,"testBrier":0.23934876035639469,"testCompounds":16,"split":"stratified random molecule split; not scaffold split"}'
WHERE name = 'AChE SMILES n-gram logistic baseline'
  AND version = '0.1.0'
  AND activity_code = 'imppat_ic50_e662faf14ac2';
