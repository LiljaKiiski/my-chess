import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.Point;
import javax.swing.ImageIcon;
import java.util.ArrayList;

//MyMouseListener Class
public class MyMouseListener implements MouseListener{
	private enum State { 
		NO_SELECTION, 
		SELECTED_PIECE 
	}

	private Main main; //Where all game variables are
	private State state;
	private Piece lastSelected;
	private int movesSinceLastEat;

	//Constructor
	public MyMouseListener(Main main){
		this.main = main;
		state = State.NO_SELECTION;
	}

	@Override
        public void mouseClicked(MouseEvent e) {
		Tile selected = currentSelectedTile(e.getPoint());
		
		switch (state){
			//No chess piece selected previously
			case NO_SELECTION:
				//Chess piece selected
				if (selected instanceof Piece){
					state = State.SELECTED_PIECE;
					lastSelected = (Piece)selected;
					highlightPossibles(lastSelected);
				}
				break;

			//Chess piece selected previously
			case SELECTED_PIECE:
				//Move piece
				if (lastSelected.validPossibleMoves().contains(selected)){
					//Add move to history
                                        main.history.add(new History(lastSelected, selected));
				
					lastSelected.move(selected.x, selected.y); //Move last selected piece
					state = State.NO_SELECTION;
					movesSinceLastEat++;

					//Reset count for 50 move rule for draw
					if (selected instanceof Piece || (lastSelected instanceof Piece && lastSelected.type.equals("pawn"))){
						movesSinceLastEat = 0;
					}

				//Don't move new selected piece
				} else if (selected instanceof Piece){
                                        lastSelected = (Piece)selected;
					highlightPossibles(lastSelected);

				} else {
					state = State.NO_SELECTION;
				}
				break;
		}
		Grid grid = main.grid;

		//Black Checkmate ✓
		if (grid.setW.playerLost(grid.setB)){
			main.frame.setTitle("Black Wins");
			main.frame.removeMouseListener(this);
		
		//White Checkmate ✓
		} else if (grid.setB.playerLost(grid.setW)){
			main.frame.setTitle("White Wins");
                        main.frame.removeMouseListener(this);
		
		// Draw Insufficient Material ✓
		} else if (grid.setW.drawInsufficientMaterial(main.painter, grid.setB)){
			main.frame.setTitle("Draw - Insufficient Material");
                        main.frame.removeMouseListener(this);
		
		//Draw Stalemate ✓
		} else if (grid.setW.drawStalemate(grid.setB) || grid.setB.drawStalemate(grid.setW)){
			main.frame.setTitle("Draw - Stalemate");
                        main.frame.removeMouseListener(this);
		
		//Draw Fifty Move Rule ✓
		} else if (movesSinceLastEat >= 100){
			main.frame.setTitle("Draw - 50 Move Rule");
                        main.frame.removeMouseListener(this);
		
		//Draw Threefold Repition.... ugh
		} else if (false){
			main.frame.setTitle("Draw - Threeforld Repitition");
			main.frame.removeMouseListener(this);
		}

		main.painter.repaint();
	}

	//Return selected tile and update variables based on selection
	public Tile currentSelectedTile(Point mousePoint){
		Tile selected = null;
		
		Grid grid = main.grid;

		for (int x = 0; x < grid.grid.length; x++){
                        for (int y = 0; y < grid.grid[x].length; y++){
				Tile tile = grid.grid[x][y];
				//Mouse on
				if (tile.mouseOn(mousePoint)){
					tile.selected = true;
					tile.possible = false;
					selected = tile;
				//Mouse not on
				} else {
					tile.selected = false;
					tile.possible = false;
				}
			}
		}
		return selected;
	}

	//Highlight possibles
	public void highlightPossibles(Piece selected){
		ArrayList<Tile> possibles = selected.validPossibleMoves();

		//Set boolean possible to true, so painter paints it as possible
		for (int x = 0; x < possibles.size(); x++){
			possibles.get(x).possible = true;
		}
	}
	
	@Override
        public void mousePressed(MouseEvent e) { }

        @Override
        public void mouseReleased(MouseEvent e) { }

        @Override
        public void mouseEntered(MouseEvent e) { }

        @Override
        public void mouseExited(MouseEvent e) { }
}
