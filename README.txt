This project is my attempt to write a chess program using a deep-learning algorithm much like AlphaZero.
As it will be playing itself multiple times over, and I've written it in a garbage-collecting language (Kotlin),
I'm taking care to write it as efficiently as possible.  At the heart of this is my attempt allocate the board
ONCE, then applying changes to this board as the move tree is searched (the 'apply' function), and undoing those
changes as branches are abandoned (the 'rollback' function).

As work progressed I found myself more and more wanting a complete test dataset, consisting of a large set
of various intricate board positions (represented as a FEN string), and the resulting next board state after
one ply of search (i.e. all the possible moves from the initial board position).  Such a database would
probably be a great resource for anyone writing their own chess program, and I even suspect a lot of existing
programs would fail some test cases.  There is a partially completed test (SearchNodeTest) here to do that
sort of test but the test data is contained in the test and is sorely lacking.

I also came to realize that standard FEN notation for a chess position is unfortunately not completely adequate
for this test data set, as it contains no indication of whether (say) white would cause a threefold repetition
draw if they move their knight to f6.  I am also pondering rolling my own Enhanced FEN notation to address this.

The threefold repetition problem is also the issue I'm stuck on in the project, as the memory-efficient algorithm
I had planned just doesn't seem to facilitate checking for it without keeping old copies of the board.  Still
pondering a way out of this, or through it.
