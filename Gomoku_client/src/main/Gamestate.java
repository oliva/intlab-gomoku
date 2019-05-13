package main;

import java.util.Arrays;

public class Gamestate {
	enum Field  { Empty, White, Black }
	enum Result { Unknown, Success, Invalid, PointWhite, PointBlack }

	private Field[][] state = new Field[15][15];
	public int[] score = {0, 0};
	public int turn=0;

	public Gamestate() {
		//init the playfield to all empty values
		for (Field[] line: state) {
			Arrays.fill(line, Field.Empty);
		}
	}

	private Result checkPoint (int x, int y) {
		//TODO check whether a point should be awarded
		Field color = state[x][y];
		int[][] dir = {{0,0,0}, {0,0,0}, {0,0,0}};
		for (int a = -1; a <= 1; a++) {
			for (int b = -1; b <= 1; b++) {
				try {
					for (int i = 1; i < 6; i++) {
						int tx=x+a*i, ty=y+b*i;
						if (state[tx][ty] == color) {
							System.out.println("match");
							dir[a + 1][b + 1]++;
						} else {
							System.out.println("no match");
							break;
						}
					}
				} catch (IndexOutOfBoundsException ignored) {}
			}
		}

		System.out.println("matrix\n" +
				dir[0][0] + dir[0][1] + dir[0][2] + "\n" +
				dir[1][0] + dir[1][1] + dir[1][2] + "\n" +
				dir[2][0] + dir[2][1] + dir[2][2] + "\n");

		if (dir[0][0] + dir[2][2] == 4
		 || dir[0][1] + dir[2][1] == 4
		 || dir[0][2] + dir[2][0] == 4
		 || dir[1][0] + dir[1][2] == 4) {
			if(color == Field.White) {
				score[0]++;
				return Result.PointWhite;
			} else {
				score[1]++;
				return Result.PointBlack;
			}
		}


		return Result.Success;
	}

	public Result place (int x, int y, Field stone) {
		/*move check:
		 * White places first and every odd step
		 * the field to place on must be empty
		 */
		if (stone == (turn%2 == 0 ? Field.White : Field.Black) || state[x][y] != Field.Empty)
			return Result.Invalid;
		state[x][y] = stone;
		turn++;
		return checkPoint(x,y);
	}
}
