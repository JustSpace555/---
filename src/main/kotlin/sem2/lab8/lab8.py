import numpy as np
import matplotlib.pyplot as plt
import os


def exactSolution(x, y, t, mu1, mu2, a):
    return np.cos(mu1 * x) * np.cos(mu2 * y) * np.exp(-(mu1 ** 2 + mu2 ** 2) * a * t)


def norm(currentGrid, previousGrid):
    max = 0
    for i in range(currentGrid.shape[0]):
        for j in range(currentGrid.shape[1]):
            if abs(currentGrid[i, j] - previousGrid[i, j]) > max:
                max = abs(currentGrid[i, j] - previousGrid[i, j])

    return max


path = os.path.dirname(os.path.abspath(__file__)) + "/"
x = np.loadtxt(path + "x")
y = np.loadtxt(path + "y")
t = np.loadtxt(path + "t")
alternating = np.loadtxt(path + "alternating").reshape((len(t), len(x), len(y))).T
fractionSteps = np.loadtxt(path + "fractionSteps").reshape((len(t), len(x), len(y))).T

a = 1
mu1 = 1
mu2 = 2

os.remove(path + "x")
os.remove(path + "y")
os.remove(path + "t")
os.remove(path + "alternating")
os.remove(path + "fractionSteps")

xAxis, yAxis = np.meshgrid(x, y)
analytical = exactSolution(xAxis, yAxis, t[-1], mu1, mu2, a)

errorX = []
errorY = []
errorT = []

for i in range(len(x)):
    errorY.append(max(abs(analytical[:, i] - alternating[:, i, -1])))
for j in range(len(y)):
    errorX.append(max(abs(analytical[j, :] - alternating[j, :, -1])))
for k in range(len(t)):
    errorT.append(norm(exactSolution(xAxis, yAxis, t[k], mu1, mu2, a), alternating[:, :, k]))

plt.title("График ошибок")

plt.plot(x, errorY, label="Фиксированный x в определенный момент времени")
plt.plot(y, errorX, label="Фиксированный y в определенный момент времени")
plt.plot(t, errorT, label="По 'x' и 'y' во всех временных промежутках")

plt.xlabel("x, y, t")
plt.ylabel("error")

plt.grid()
plt.legend()

fig = plt.figure()

ax = fig.add_subplot(111, projection="3d")
ax.plot_surface(xAxis, yAxis, analytical, label="Exact", color="red")
ax.plot_surface(xAxis, yAxis, alternating[:, :, -1], label="Fractional Steps")
ax.set_xlabel("x")
ax.set_ylabel("y")
ax.set_zlabel("U")

plt.show()
