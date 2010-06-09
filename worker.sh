#$ -N icfp-worker
#$ -pe mpich 1
#$ -M mark.probst@gmail.com
#$ -l h_rt=00:05:00
#$ -t 1-100:1
#$ -cwd
#$ -V

./demo/runworker.sh
