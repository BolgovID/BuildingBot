import com.google.gson.Gson;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Bot extends TelegramLongPollingBot {




    private FileIO fileIO;
    private Gson json = new Gson();

    public static void main(String[] args) throws IOException {
        ApiContextInitializer.init();
        
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        House.LoadHouse();
        try {
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }

    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            try {
                Message message = update.getMessage();
                SendMessage sendMessage;
                if (message.getText().equals(Settings.START)) {
                    sendMessage = setSendMessage(message, Space.class.toString());
                    execute(sendMessage);
                } else if (message.getText().equals(Settings.UPDATE)) {
                    House.DeleteAllInList();
                    House.LoadHouse();
                    execute(setSendMessage(message, Settings.DirectCallSpaceKeyboard));
                }
            } catch (TelegramApiException | IOException e) {
                e.printStackTrace();
            }
        } else if (update.hasCallbackQuery()) {
            String call_data = update.getCallbackQuery().getData();
            String username = String.valueOf(update.getCallbackQuery().getMessage().getChat().getId());
            String filePath = "Repository/Users/" + username;
            long chat_id = update.getCallbackQuery().getMessage().getChatId();

            if (!Files.exists(Paths.get(filePath))) {
                new File(filePath).mkdirs();
            }
            SendMessage sendMessage;
            EditMessageText editMessageText;
            try {

                if (!call_data.equals(Settings.RepeatOfSession)) {

                    if ((Type.isExists(call_data))) {
                        writeChoice(filePath, call_data, null);
                    } else {
                        writeChoice(filePath, null, call_data);
                    }

                    editMessageText = setEditMessage(update.getCallbackQuery().getMessage(), call_data);
                    sendMessage = setSendMessage(update.getCallbackQuery().getMessage(), call_data);
                    fileIO = new FileIO(filePath + "/choice.txt");
                    ChoiceJSON choice = json.fromJson(fileIO.read(), ChoiceJSON.class);
                    execute(editMessageText);
                    if (choice.IsFull()) {
                        ArrayList<House> filteredHouses = House.getHouses(choice.getType(), choice.getSpace());
                        int size = filteredHouses.size();
                        if (size != 0) {
                            for (House house : filteredHouses) {
                                execute(new SendMessage().setChatId(chat_id)
                                        .setText("[Посмотреть](" + house.getRef() + ")")
                                        .setParseMode(ParseMode.MARKDOWN));
                            }
                        } else
                            execute(new SendMessage().setChatId(chat_id).setText("Ничего не найдено"));
                        writeChoice(filePath, null, null);
                        sendMessage = setSendMessage(update.getCallbackQuery().getMessage(), Settings.EndOfSession);
                        execute(sendMessage);
                    } else {
                        execute(sendMessage);
                    }
                } else {
                    writeChoice(filePath, null, null);
                    sendMessage = setSendMessage(update.getCallbackQuery().getMessage(), Settings.DirectCallSpaceKeyboard);
                    execute(setEditMessage(update.getCallbackQuery().getMessage(), null));
                    execute(sendMessage);
                }
            } catch (IOException | TelegramApiException e) {
                e.printStackTrace();
            }
        }

    }

    private void writeChoice(String filePath, String type, String space) throws IOException {
        fileIO = new FileIO(filePath + "/choice.txt");
        if (!Files.exists(Paths.get(filePath + "/choice.txt"))) {
            fileIO.write(json.toJson(new ChoiceJSON(type, space)));
        } else {
            ChoiceJSON choice = json.fromJson(fileIO.read(), ChoiceJSON.class);
            choice.setValue(type, space);
            fileIO.write(json.toJson(choice));
        }
    }

    private SendMessage setSendMessage(Message message, String call_data) {
        SendMessage sendMessage = new SendMessage();

        if (!call_data.equals(Settings.EndOfSession)) {
            if (Space.isExists(call_data)||call_data.equals(Settings.DirectCallTypeKeyboard))
                if(House.getTypeListBySpace(call_data).size()==0)
                    sendMessage.setChatId(message.getChatId()).setText("На данный момент, в этом районе нет домов.\nНайти еще дома?")
                            .setReplyMarkup(setEndInlineKeyboard(false));
                else
                sendMessage.setChatId(message.getChatId()).setText("В этом районе есть дома класса: ")
                        .setReplyMarkup(setTypeInlineKeyboard(call_data));
            else if (message.getText().equals(Settings.UPDATE))
                sendMessage.setChatId(message.getChatId()).setText("База данных обновленна");
            else if (message.getText().equals(Settings.START) || Type.isExists(call_data)||call_data.equals(Settings.DirectCallSpaceKeyboard))
                    sendMessage.setChatId(message.getChatId()).setText("Выберите район")
                            .setReplyMarkup(setSpaceInlineKeyboard());
        } else {
            sendMessage.setChatId(message.getChatId()).setText("Найти еще дома?")
                    .setReplyMarkup(setEndInlineKeyboard(false));
        }
        return sendMessage;
    }

    private EditMessageText setEditMessage(Message message, String filter) {
        EditMessageText edit_message = new EditMessageText();

        if (filter == null) {
            edit_message.setChatId(message.getChatId()).setMessageId(message.getMessageId()).setText("Что-нибудь еще?")
                    .setReplyMarkup(setEndInlineKeyboard(true));
        } else if (Space.isExists(filter)) {
            edit_message.setChatId(message.getChatId()).setMessageId(message.getMessageId())
                    .setText("Вы выбрали " + Space.valueOf(filter).toString());
        } else if (Type.isExists(filter)) {
            edit_message.setChatId(message.getChatId()).setMessageId(message.getMessageId())
                    .setText("Вы выбрали " + Type.valueOf(filter).toString());
        }
        return edit_message;
    }

    private InlineKeyboardMarkup setEndInlineKeyboard(boolean isRepeat) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        rowsInline.add(new ArrayList<InlineKeyboardButton>());
        rowsInline.get(rowsInline.size() - 1).add(new InlineKeyboardButton()
                .setText("Перейти на канал").setUrl(Settings.ChannelLink));
        rowsInline.get(rowsInline.size() - 1).add(new InlineKeyboardButton()
                .setText("Поделиться ботом").setUrl(Settings.BotLink));
        if (!isRepeat) {
            System.out.println(isRepeat);
            rowsInline.add(new ArrayList<InlineKeyboardButton>());
            rowsInline.get(rowsInline.size() - 1).add(new InlineKeyboardButton()
                    .setText("Повтор").setCallbackData(Settings.RepeatOfSession));
        }
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    private InlineKeyboardMarkup setTypeInlineKeyboard(String call_data) {
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            ArrayList<String> TypeList=House.getTypeListBySpace(call_data);
            int length = TypeList.size();
            if (length > Settings.TransitionArea) {
                int count = (length % 2 == 0) ? (int) Math.ceil((double) length / 2) + 1 : (int) Math.ceil((double) length / 2);

                for (int i = 0; i < count; i++) {
                    rowsInline.add(new ArrayList<InlineKeyboardButton>());
                    if (i == 0) {
                        rowsInline.get(i).add(new InlineKeyboardButton()
                                .setText(Type.getNameById(TypeList.get(i * 2)))
                                .setCallbackData(TypeList.get(i * 2)));
                        continue;
                    } else if (length % 2 == 0 && i == count - 1) {
                        rowsInline.get(i).add(new InlineKeyboardButton()
                                .setText(Type.getNameById(TypeList.get(i * 2 - 1)))
                                .setCallbackData(TypeList.get(i * 2 - 1)));
                        break;
                    }
                    rowsInline.get(i).add(new InlineKeyboardButton()
                            .setText(Type.getNameById(TypeList.get(i * 2 - 1)))
                            .setCallbackData(TypeList.get(i * 2 - 1)));
                    rowsInline.get(i).add(new InlineKeyboardButton()
                            .setText(Type.getNameById(TypeList.get(i * 2)))
                            .setCallbackData(TypeList.get(i * 2)));
                }
            } else {
                for (int i = 0; i < length; i++) {
                    rowsInline.add(new ArrayList<InlineKeyboardButton>());
                    rowsInline.get(i).add(new InlineKeyboardButton()
                            .setText(Type.getNameById(TypeList.get(i)))
                            .setCallbackData(TypeList.get(i)));
                }
            }

            markupInline.setKeyboard(rowsInline);
            return markupInline;
    }

    private InlineKeyboardMarkup setSpaceInlineKeyboard() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        int length = Space.values().length;
        if (length > Settings.TransitionArea) {
            int count = (length % 2 == 0) ? (int) Math.ceil((double) length / 2) + 1 : (int) Math.ceil((double) length / 2);
            for (int i = 0; i < count; i++) {
                rowsInline.add(new ArrayList<InlineKeyboardButton>());
                if (i == 0) {
                    rowsInline.get(i).add(new InlineKeyboardButton()
                            .setText(Space.values()[i * 2].toString())
                            .setCallbackData(Space.values()[i * 2].name()));
                    continue;
                } else if (length % 2 == 0 && i == count - 1) {
                    rowsInline.get(i).add(new InlineKeyboardButton()
                            .setText(Space.values()[i * 2 - 1].toString())
                            .setCallbackData(Space.values()[i * 2 - 1].name()));
                    break;
                }
                rowsInline.get(i).add(new InlineKeyboardButton()
                        .setText(Space.values()[i * 2 - 1].toString())
                        .setCallbackData(Space.values()[i * 2 - 1].name()));
                rowsInline.get(i).add(new InlineKeyboardButton()
                        .setText(Space.values()[i * 2].toString())
                        .setCallbackData(Space.values()[i * 2].name()));
            }
        } else {
            for (int i = 0; i < length; i++) {
                rowsInline.add(new ArrayList<InlineKeyboardButton>());
                rowsInline.get(i).add(new InlineKeyboardButton()
                        .setText(Space.values()[i].toString())
                        .setCallbackData(Space.values()[i].name()));
            }
        }
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    public String getBotUsername() {
        return Settings.USERNAME;
    }

    public String getBotToken() {
        return Settings.TOKEN;
    }
}
