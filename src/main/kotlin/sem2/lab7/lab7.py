import numpy as np
import matplotlib.pyplot as plt
import os


def exactSolution(x, y):
    return x ** 2 - y ** 2


path = os.path.dirname(os.path.abspath(__file__)) + "/"
x = np.loadtxt(path + "x")
y = np.loadtxt(path + "y")

liebman = np.loadtxt(path + "liebman")
relaxation = np.loadtxt(path + "relaxation")
zeidel = np.loadtxt(path + "zeidel")

os.remove(path + "x")
os.remove(path + "y")
os.remove(path + "liebman")
os.remove(path + "relaxation")
os.remove(path + "zeidel")

X, Y = np.meshgrid(x, y)
analytical = exactSolution(X, Y)

figure = plt.figure()
axx = figure.add_subplot(111, projection="3d")
axx.plot_surface(X, Y, abs(analytical - liebman))
axx.plot_surface(X, Y, abs(analytical - relaxation))
axx.plot_surface(X, Y, abs(analytical - zeidel))
fig = plt.figure()
ax = fig.add_subplot(111, projection="3d")
ax.plot_surface(X, Y, analytical, label="Аналитическое")
ax.plot_surface(X, Y, liebman, label="Либман")
ax.plot_surface(X, Y, relaxation, label="Релаксация")
ax.plot_surface(X, Y, zeidel, label="Зейдель")
plt.show()
