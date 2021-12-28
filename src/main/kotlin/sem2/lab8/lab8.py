import numpy as np
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D
import os

def exactSolution(x, y, t, mu1, mu2, a):
    return np.cos(mu1 * x) * np.cos(mu2 * y) * np.exp(-(mu1 ** 2 + mu2 ** 2) * a * t)

def norm(curr_grid, prev_grid):
    max = 0
    for i in range(curr_grid.shape[0]):
        for j in range(curr_grid.shape[1]):
            if abs(curr_grid[i, j] - prev_grid[i, j]) > max:
                max = abs(curr_grid[i, j] - prev_grid[i, j])

    return max

x = np.loadtxt("x")
y = np.loadtxt("y")
alternating = np.loadtxt("alternating")
fractionSteps = np.loadtxt("fractionSteps")

os.remove("x")
os.remove("y")
os.remove("alternating")
os.remove("fractionSteps")

X, Y = np.meshgrid(x, y)
analytical = exactSolution(X, Y, t[-1], mu1, mu2, a)

errorX = []
errorY = []
errorT = []

for i in range(len(x)):
    errorY.append(max(abs(analytical[:, i] - alternating[:, i, -1])))

for j in range(len(y)):
    errorX.append(max(abs(analytical[j, :] - alternating[j, :, -1])))

for k in range(len(t)):
    errorT.append(norm(exactSolution(X, Y, t[k], mu1, mu2, a), alternating[:, :, k]))

plt.title("График ошибок")

plt.plot(x, error_y, label = "При фиксированном x в выбранный момент времени", color = "red")
plt.plot(y, error_x, label = "При фиксированном y в выбранный момент времени", color = "blue")
plt.plot(t, error_t, label = "По 'x' и 'y' во всех временных промежутках", color = "green")

plt.xlabel("x, y, t")
plt.ylabel("error")

plt.grid()
plt.legend()

fig = plt.figure()

ax = fig.add_subplot(111, projection="3d")
ax.plot_surface(X, Y, analitical, label="Exact", color="red")
ax.plot_surface(X, Y, fractionsteps[:,:,-1], label="Fractional Steps")
ax.set_xlabel("x")
ax.set_ylabel("y")
ax.set_zlabel("U")

plt.show()