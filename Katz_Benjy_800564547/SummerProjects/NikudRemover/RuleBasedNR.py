#rule based nikud remover
#rule one: in the case of an u sound replace with a vav


kubutz = "\u05bb"
holam = "\u05b9"
holam_malei = "\u05ba"
def placeVav(word):
    withVav = ""
    for c in word:
        if c == kubutz:
            withVav = withVav+"ו"
        elif c == holam or c == holam_malei:
            withVav = withVav+"ו"
            #with exception
        else:
            withVav = withVav+c
    return withVav