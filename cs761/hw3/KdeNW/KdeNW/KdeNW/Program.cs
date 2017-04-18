using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using System.Threading.Tasks;

namespace KdeNW
{
    class Program
    {
        public const  Double MIN_EXP = 1.2;
        static void Main(string[] args)
        {
            List<double> xVals = new List<double>();
            List<double> yVals = new List<double>();
            using (var fs = File.OpenRead(@"C:\Users\brendanx\HWSpring2017\cs761\hw3\ice_parsed.csv"))
            using (var reader = new StreamReader(fs))
            {
                
                while (!reader.EndOfStream)
                {
                    var line = reader.ReadLine();
                    var values = line.Split(',');

                    xVals.Add(Double.Parse(values[0]));
                    yVals.Add(Double.Parse(values[1]));
                }
            }
            var xArr = xVals.ToArray();
            var yArr = yVals.ToArray();
            var minScore = Double.PositiveInfinity;
            var minExp = -1.0;
            /*
            for (double exp = -1; exp <= 2; exp += 0.1)
            {
                Math.Round(exp, 1);

                var aligner = new KdeNW(xArr,yArr,Math.Pow(10,exp),new GaussianKernel());
                var score = Math.Round(aligner.GetLeaveOneOutScore(),5);
                if (score < minScore)
                {
                    minScore = score;
                    minExp = exp;
                }
                Console.WriteLine("h = 10^"+exp+", LeaveOneOut Score = " + score );
            }
            
            Console.WriteLine("Min score: " + minScore+ " Min Exp: " + minExp);
            Console.WriteLine("Press ESC to stop");*/
            var aligner = new KdeNW(xArr,yArr,Math.Pow(10,2),new GaussianKernel());
            for (var year = 1855; year <= 2016; year++)
            {
                Console.WriteLine(year+","+aligner.GetYFor(year));
            }
            do
            {
                while (!Console.KeyAvailable)
                {
                    // Do something
                }
            } while (Console.ReadKey(true).Key != ConsoleKey.Escape);

        }

        
    }

    public class KdeNW
    {
        private double[] _xvals;
        private double[] _yvals;
        private double _bandwidth;
        private Kernel _kernel;
        public KdeNW(double[] xvals, double[] yvals, double bandwidth, Kernel kernel)
        {
            if(bandwidth <= 0)
                throw new Exception("Bandwidth must be positive");
            if(xvals.Length != yvals.Length)
                throw new Exception("xVals and yVals do not match in length");
            _xvals = xvals;
            _yvals = yvals;
            _bandwidth = bandwidth;
            _kernel = kernel;
        }

        public double GetYFor(double x)
        {
            double sum = 0;
            double weightedSum = 0;
            double[] kVals = new double[_xvals.Length];
            for(int i = 0 ; i < _xvals.Length; i ++)
            {
                var xVal = _xvals[i];
                var kVal = _kernel.GetValue((xVal - x) / _bandwidth);
                kVals[i] = kVal;
                sum += kVal;
            }
            for (int i = 0; i < kVals.Length; i++)
            {
                weightedSum += kVals[i] * _yvals[i] / sum;
            }
            return weightedSum;
        }

        public double GetLeaveOneOutScore()
        {
            double score = 0;
            for (int i = 0; i < _xvals.Length; i++)
            {
                var kSum = 0.0;
                var leftOutX = _xvals[i];
                for (int j = 0; j < _xvals.Length;j++)
                {
                    var xVal = _xvals[j];
                    var kVal = _kernel.GetValue((j == i ? 0:(xVal - leftOutX) / _bandwidth));
                    kSum += kVal;
                }
                var weightedDiff = (GetYFor(_xvals[i]) - _yvals[i])/(1 - _kernel.GetValue(0) / kSum);
                score += weightedDiff * weightedDiff / _xvals.Length;
            }
            return score;
        }
    }

    public abstract class Kernel
    {
        public abstract double Range { get; protected set; }

        public abstract double GetValue(double x);

    }

    public class GaussianKernel : Kernel
    {
        public override double Range { get { return double.PositiveInfinity; }
            protected set {}
        }
        public override double GetValue(double x)
        {
            return 1 / (2 * Math.PI) * Math.Exp(-(x * x) / 2);
        }
    }
}
