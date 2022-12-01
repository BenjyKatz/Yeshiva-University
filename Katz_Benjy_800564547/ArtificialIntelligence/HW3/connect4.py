import copy

from pickle import FALSE, TRUE
import numpy as np
import random
from termcolor import colored  # can be taken out if you don't like it...

# # # # # # # # # # # # # # global values  # # # # # # # # # # # # # #
ROW_COUNT = 6
COLUMN_COUNT = 7

RED_CHAR = colored('X', 'red')  # RED_CHAR = 'X'
BLUE_CHAR = colored('O', 'blue')  # BLUE_CHAR = 'O'

EMPTY = 0
RED_INT = 1
BLUE_INT = 2


# # # # # # # # # # # # # # functions definitions # # # # # # # # # # # # # #

def create_board():
    """creat empty board for new game"""
    board = np.zeros((ROW_COUNT, COLUMN_COUNT), dtype=int)
    return board


def drop_chip(board, row, col, chip):
    """place a chip (red or BLUE) in a certain position in board"""
    board[row][col] = chip


def is_valid_location(board, col):
    """check if a given column in the board has a room for extra dropped chip"""
    return board[ROW_COUNT - 1][col] == 0

def get_next_open_row(board, col):
    """assuming column is available to drop the chip,
    the function returns the lowest empty row  """
    for r in range(ROW_COUNT):
        if board[r][col] == 0:
            return r

def print_board(board):
    """print current board with all chips put in so far"""
    # print(np.flip(board, 0))
    print(" 1 2 3 4 5 6 7 \n" "|" + np.array2string(np.flip(np.flip(board, 1)))
          .replace("[", "").replace("]", "").replace(" ", "|").replace("0", "_")
          .replace("1", RED_CHAR).replace("2", BLUE_CHAR).replace("\n", "|\n") + "|")

def game_is_won(board, chip):
    """check if current board contain a sequence of 4-in-a-row of in the board
     for the player that play with "chip"  """

    winning_Sequence = np.array([chip, chip, chip, chip])
    # Check horizontal sequences
    for r in range(ROW_COUNT):
        if "".join(list(map(str, winning_Sequence))) in "".join(list(map(str, board[r, :]))):
            return True
    # Check vertical sequences
    for c in range(COLUMN_COUNT):
        if "".join(list(map(str, winning_Sequence))) in "".join(list(map(str, board[:, c]))):
            return True
    # Check positively sloped diagonals
    for offset in range(-2, 4):
        if "".join(list(map(str, winning_Sequence))) in "".join(list(map(str, board.diagonal(offset)))):
            return True
    # Check negatively sloped diagonals
    for offset in range(-2, 4):
        if "".join(list(map(str, winning_Sequence))) in "".join(list(map(str, np.flip(board, 1).diagonal(offset)))):
            return True

def get_valid_locations(board):
    valid_locations = []
    for col in range(COLUMN_COUNT):
        if is_valid_location(board, col):
            valid_locations.append(col)
    return valid_locations

def MoveRandom(board, color):
    valid_locations = get_valid_locations(board)
    column = random.choice(valid_locations)   # you can replace with input if you like... -- line updated with Gilad's code-- thanks!
    row = get_next_open_row(board, column)
    drop_chip(board, row, column, color)

def utility(board, chip):
    total_utility = 0
    for i in range(2):
        total_utility+=(count_in_a_row(board, chip, i+2)*(i+2)*(i+2))
    #winning has a lot of utility
    if(count_in_a_row(board, chip, 4)): 
        total_utility += 1000000
        return total_utility
    #setting a double trap
    if(double_trap(board, chip) is TRUE): total_utility += 5000
    total_utility+= three_with_space(board, chip)*15
    total_utility+= two_with_space(board, chip)*8
    #total_utility+= one_with_space(board, chip)
    

    return total_utility-utility_of_other(board, chip)

def utility_of_other(board, chip):
    if(chip == RED_INT): chip = BLUE_INT
    else: chip = RED_INT
    total_utility = 0
    for i in range(2):
        total_utility+=(count_in_a_row(board, chip, i+2)*(i+2)*(i+2))
    #winning has a lot of utility
    if(count_in_a_row(board, chip, 4)): 
        total_utility += 1000000
        return total_utility
    #setting a double trap
    if(double_trap(board, chip) is TRUE): total_utility += 5000
    total_utility+= three_with_space(board, chip)*15
    total_utility+= two_with_space(board, chip)*8
    #total_utility+= one_with_space(board, chip)


    return total_utility

def count_in_a_row(board, chip, consecutive):
    total = 0
    winning_Sequence = np.array([chip, chip, chip, chip])
    if(int(consecutive)==2):
        winning_Sequence = np.array([chip, chip])
    if(int(consecutive)==3):
        winning_Sequence = np.array([chip, chip, chip])
    if(int(consecutive)==4):
        winning_Sequence = np.array([chip, chip, chip, chip])
    # Check horizontal sequences
    for r in range(ROW_COUNT):
        if "".join(list(map(str, winning_Sequence))) in "".join(list(map(str, board[r, :]))):
            total += 1
    # Check vertical sequences
    for c in range(COLUMN_COUNT):
        if "".join(list(map(str, winning_Sequence))) in "".join(list(map(str, board[:, c]))):
            total += 1
    # Check positively sloped diagonals
    for offset in range(-2, 4):
        if "".join(list(map(str, winning_Sequence))) in "".join(list(map(str, board.diagonal(offset)))):
            total += 1
    # Check negatively sloped diagonals
    for offset in range(-2, 4):
        if "".join(list(map(str, winning_Sequence))) in "".join(list(map(str, np.flip(board, 1).diagonal(offset)))):
            total += 1
    return total

def double_trap(board, chip):
    the_sequence = np.array([0, chip, chip, chip, 0])
    # Check horizontal sequences
    for r in range(ROW_COUNT):
        if "".join(list(map(str, the_sequence))) in "".join(list(map(str, board[r, :]))):
            if(r == 0): return TRUE #double trap on bottom
            else:  
                start = ("".join(list(map(str, board[r, :])))).index("".join(list(map(str, the_sequence))))
                if(board[r-1, start] != 0 & board[r-1, start+4] !=0 ): return TRUE

    # Check positively sloped diagonals
    for offset in range(-2, 4):
        if "".join(list(map(str, the_sequence))) in "".join(list(map(str, board.diagonal(offset)))):
            if(offset>-1):
                start = ("".join(list(map(str, board.diagonal(offset))))).index("".join(list(map(str, the_sequence))))
                if(start>0):
                    if(board[start-1, offset] != 0 & board[start+3, offset+4] !=0 ): return TRUE
                if(start == 0):
                    if(board[start+3, offset+4] !=0 ): return TRUE
            if(offset<0):
                start = ("".join(list(map(str, board.diagonal(offset))))).index("".join(list(map(str, the_sequence))))
                if(board[start-1, offset] != 0 & board[start+3, offset+4] !=0 ): return TRUE


    # Check negatively sloped diagonals
    for offset in range(-2, 4):
        if "".join(list(map(str, the_sequence))) in "".join(list(map(str, np.flip(board, 1).diagonal(offset)))):
            if(offset>-1):
                start = ("".join(list(map(str, np.flip(board, 1).diagonal(offset))))).index("".join(list(map(str, the_sequence))))
                if(start>0):
                    if(np.flip(board,1)[start-1, offset] != 0 & np.flip(board,1)[start+3, offset+4] !=0 ): return TRUE
                if(start == 0):
                    if(np.flip(board,1)[start+3, offset+4] !=0 ): return TRUE
            if(offset<0):
                start = ("".join(list(map(str, np.flip(board, 1).diagonal(offset))))).index("".join(list(map(str, the_sequence))))
                if(np.flip(board,1)[start-1, offset] != 0 & np.flip(board,1)[start+3, offset+4] !=0 ): return TRUE
    return FALSE

def three_with_space(board, chip):
    total = 0
    for i in range(4):
        if(i == 0): winning_Sequence = np.array([0, chip, chip, chip])
        if(i == 1): winning_Sequence = np.array([chip, 0, chip, chip])
        if(i == 2): winning_Sequence = np.array([chip, chip, 0, chip])
        if(i == 3): winning_Sequence = np.array([chip, chip, chip, 0])
        # Check horizontal sequences
        for r in range(ROW_COUNT):
            if "".join(list(map(str, winning_Sequence))) in "".join(list(map(str, board[r, :]))):
                total += 1
        # Check vertical sequences
        for c in range(COLUMN_COUNT):
            if "".join(list(map(str, winning_Sequence))) in "".join(list(map(str, board[:, c]))):
                total += 1
        # Check positively sloped diagonals
        for offset in range(-2, 4):
            if "".join(list(map(str, winning_Sequence))) in "".join(list(map(str, board.diagonal(offset)))):
                total += 1
        # Check negatively sloped diagonals
        for offset in range(-2, 4):
            if "".join(list(map(str, winning_Sequence))) in "".join(list(map(str, np.flip(board, 1).diagonal(offset)))):
                total += 1
    return total
def two_with_space(board, chip):
    total = 0
    
    for i in range(3):
        winning_Sequence = np.array([chip, chip, chip])
        winning_Sequence[i] = 0
        # Check horizontal sequences
        for r in range(ROW_COUNT):
            if "".join(list(map(str, winning_Sequence))) in "".join(list(map(str, board[r, :]))):
                total += 1
        # Check vertical sequences
        for c in range(COLUMN_COUNT):
            if "".join(list(map(str, winning_Sequence))) in "".join(list(map(str, board[:, c]))):
                total += 1
        # Check positively sloped diagonals
        for offset in range(-2, 4):
            if "".join(list(map(str, winning_Sequence))) in "".join(list(map(str, board.diagonal(offset)))):
                total += 1
        # Check negatively sloped diagonals
        for offset in range(-2, 4):
            if "".join(list(map(str, winning_Sequence))) in "".join(list(map(str, np.flip(board, 1).diagonal(offset)))):
                total += 1
    return total
def one_with_space(board, chip):
    total = 0
    winning_Sequence = np.array([chip, chip, chip])
    for j in range(3):
        for i in range(j+1, 3):
            winning_Sequence[i] = 0
            winning_Sequence[j] = 0
            # Check horizontal sequences
            for r in range(ROW_COUNT):
                if "".join(list(map(str, winning_Sequence))) in "".join(list(map(str, board[r, :]))):
                    total += 1
            # Check vertical sequences
            for c in range(COLUMN_COUNT):
                if "".join(list(map(str, winning_Sequence))) in "".join(list(map(str, board[:, c]))):
                    total += 1
            # Check positively sloped diagonals
            for offset in range(-2, 4):
                if "".join(list(map(str, winning_Sequence))) in "".join(list(map(str, board.diagonal(offset)))):
                    total += 1
            # Check negatively sloped diagonals
            for offset in range(-2, 4):
                if "".join(list(map(str, winning_Sequence))) in "".join(list(map(str, np.flip(board, 1).diagonal(offset)))):
                    total += 1
    return total
# # # # # # # # # # # # # # the bot # # # # # # # # # # # # # #
def chose_move(board, chip):
    #if it can win, do it
    for col in range(1, COLUMN_COUNT+1):
        board_copy = copy.deepcopy(board)
        if(is_valid_location(board_copy, col-1)):
            row = get_next_open_row(board_copy, col-1)
            drop_chip(board_copy, row, col-1, chip)
            if(utility(board_copy, chip)>10000): 
                move = col
                row = get_next_open_row(board, move-1)
                drop_chip(board, row, move-1, chip)
                return move
    #reverse the chip
    if(chip == RED_INT): chip = BLUE_INT
    else: chip = RED_INT
    #if the other person will win, block
    for col in range(1, COLUMN_COUNT+1):
        board_copy = copy.deepcopy(board)
        if(is_valid_location(board_copy, col-1)):
            row = get_next_open_row(board_copy, col-1)
            drop_chip(board_copy, row, col-1, chip)
            if(utility(board_copy, chip)>10000): 
                move = col
                #reverse it back and block
                if(chip == RED_INT): chip = BLUE_INT
                else: chip = RED_INT
                row = get_next_open_row(board, move-1)
                drop_chip(board, row, move-1, chip)
                return move
    if(chip == RED_INT): chip = BLUE_INT
    else: chip = RED_INT
    result = max(board, chip, 2)
    move = result[1] 
    row = get_next_open_row(board, move-1)
    drop_chip(board, row, move-1, chip)
    return move

def agent1move(board):
    return chose_move(board, BLUE_INT)-1

def max(board, chip, d):
    value = utility(board, chip)
    next_states = []
    tmp_max = -1000000000
    best_move = 0
    for col in range(1, COLUMN_COUNT+1):
        board_copy = copy.deepcopy(board)
        if(is_valid_location(board_copy, col-1)):
            row = get_next_open_row(board_copy, col-1)
            drop_chip(board_copy, row, col-1, chip)
            next_states.append([board_copy, col])
    for state in next_states:
        if(d>0):
            tmp = min(copy.deepcopy(state[0]), chip, d-1)[0]
        else:
            tmp = utility(copy.deepcopy(state[0]),chip)
  
        if(tmp>tmp_max):
            tmp_max = tmp
            best_move = state[1]

    return[tmp_max, best_move]


def min(board, chip, d):
    #reverse the chip
    if(chip == RED_INT): chip = BLUE_INT
    else: chip = RED_INT
    next_states = []
    tmp_min = 1000000000
    best_state = np.array
    for col in range(1, COLUMN_COUNT+1):
        board_copy = copy.deepcopy(board)
        if(is_valid_location(board_copy, col-1)):
            row = get_next_open_row(board_copy, col-1)
            drop_chip(board_copy, row, col-1, chip)
            next_states.append(board_copy)
    #reverse it back
    if(chip == RED_INT): chip = BLUE_INT
    else: chip = RED_INT
    for state in next_states:
        tmp = max(copy.deepcopy(state), chip, d-1)[0]
        if(tmp<tmp_min):
            tmp_min = tmp
            best_state = copy.deepcopy(state)

    return[tmp_min, best_state]
    
# # # # # # # # # # # # # # main execution of the game # # # # # # # # # # # # # #
bot_wins = 0

turn = 0

board = create_board()
print_board(board)
game_over = False

while not game_over:
    if turn % 2 == 0:
        col = int(input("RED please choose a column(1-7): "))
        #col = random.randint(1,7)
        while col > 7 or col < 1:
            col = int(input("Invalid column, pick a valid one: "))
            #col = random.randint(1,7)
        while not is_valid_location(board, col - 1):
            col = int(input("Column is full. pick another one..."))
            #col = random.randint(1,7)
        col -= 1

        row = get_next_open_row(board, col)
        drop_chip(board, row, col, RED_INT)

    if turn % 2 == 1 and not game_over:
        #MoveRandom(board,BLUE_INT)
        #chose_move(board,BLUE_INT)
        print("here "+str(agent1move(board)))

    print_board(board)
    print(utility(board, BLUE_INT))
    if game_is_won(board, RED_INT):
        game_over = True
        print(colored("Red wins!", 'red'))
    if game_is_won(board, BLUE_INT):
        game_over = True
        print(colored("Blue wins!", 'blue'))
        bot_wins+=1
    if len(get_valid_locations(board)) == 0:
        game_over = True
        print(colored("Draw!", 'blue'))
    turn += 1

#tmp = copy.deepcopy(board)