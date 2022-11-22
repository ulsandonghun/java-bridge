package bridge;

import java.util.List;
import types.BridgeType;
import types.MoveResult;
import types.RetryCommand;
import views.InputView;
import views.OutputView;


public class GameMachine {
    private InputView ui = new InputView();
    private OutputView view = new OutputView();
    private BridgeMaker bridgeMaker = new BridgeMaker(new BridgeRandomNumberGenerator());

    private List<BridgeType> bridge;
    private BridgeGame bridgeGame;
    private int gameCounter;

    public GameMachine() {
        view.printStart();
        view.printBridgeSizeRequest();
        bridge = makeBridge();
        bridgeGame = new BridgeGame(bridge);
        gameCounter=0;
    }

    public void run() {
        RetryCommand respond;
        do {
            MoveResult gameResult = play();
            respond = askRetry(gameResult);
        } while (respond == RetryCommand.RETRY);
        end();
    }


    private RetryCommand askRetry(MoveResult gameResult) {
        RetryCommand respond = RetryCommand.QUIT;
        if (gameResult == MoveResult.FAIL) {
            view.printRestartRequest();
            respond = RetryCommand.of(ui.readGameCommand());
        }
        return respond;

    }

    private void end() {
        view.printResult();
        view.printGameCount(gameCounter);
    }


    private MoveResult play() {
        gameCounter++;
        for (int location = 0; location < bridge.size(); location++) {
            MoveResult moveResult = move();
            if (moveResult == MoveResult.FAIL) {
                view.printMap(bridge, location, MoveResult.FAIL);
                return MoveResult.FAIL;
            }
            view.printMap(bridge, location, MoveResult.PASS);
        }
        return MoveResult.PASS;

    }

    private MoveResult move() {
        view.printMoveTypeRequest();
        BridgeType userInput = BridgeType.of(ui.readMoving());
        MoveResult moveResult = bridgeGame.move(userInput);
        return moveResult;
    }


    private List<BridgeType> makeBridge() {
        List<String> bridge = bridgeMaker.makeBridge(
                ui.readBridgeSize()
        );
        return BridgeType.toBridge(bridge);
    }
}
