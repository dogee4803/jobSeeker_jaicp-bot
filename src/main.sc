require: functions.js
require: slotfilling/slotFilling.sc
  module = sys.zb-common
theme: /

    # Начало и приветствие
    state: Start
        q!: $regex</start>
        random:
            a: Здраствуйте! Этот бот служит для нахождения доступных вакансий. Чем могу вам помочь?
            a: Приветсвую, я бот для поиска вакансии. Как я могу вам помочь?
        buttons:
            "Заполнить анкету" -> /Survey
            "Поиск" -> /HandleApiResponse

    state: Hello
        intent!: /привет
        a: Ещё раз здраствуйте)


    # Анкетирование пользователя
    state: Survey
        intent!: /анкета
        random: 
            a: Отлично! Давайте заполним небольшую анкету.
            a: Хорошо, заполним анкету для просмотра вакансий.
            a: Замечательно! Приступим к заполнению анкеты.
        a: Какая профессия вас интересует?

        state: AwaitJob
            # intent: /профессия
            q: * @profession *
            a: В каком регионе ищете работу? Напишите пожалуйста номер
            script:
                $session.survey.job = $request.query;
            
            state: AwaitRegion
                q: * @region *
                a: В каком конкретно городе ищете работу?
                script:
                    $session.survey.region = $request.query;
            
                state: AwaitCity
                    q: * @mystem.geo *
                    a: Какой тип занятости вас интересует? Полная, временная или частичная занятость?
                    script:
                        $session.survey.city = $request.query;
                        
                    state: AwaitEmployment
                        q: * @employment *
                        a: От какого размера заработной платы начинать искать?
                        script:
                            $session.survey.employment = $request.query;
                        
                        state: AwaitSalary
                            q: * @duckling.number *
                            a: готово?
                            script:
                                $session.survey.salary = $request.query;
                        
                            state: SurveyComplete
                                q: готово
                                a: Спасибо! Ваша анкета заполнена. Вот что вы указали:
                                a: Профессия: {{ $session.survey.job }}. Регион: {{ $session.survey.region }}. Город: {{ $session.survey.city }}. Тип занятости: {{ $session.survey.employment }}. Зарплата: {{ $session.survey.salary }} руб.

    
    state: HandleApiResponse
        intent!: /поиск
        script:
            # Выполняем запрос через API для поиска вакансий
            fetchVacancies().then(function (res) {
                if (res.status === '200' && res.results.vacancies.length > 0) {
                    $session.vacancies = res.results.vacancies;
                    $session.page = 0;
                    $reactions.answer("Вакансии успешно найдены! Перехожу к отображению...");
                    showPage($session.page, $session.vacancies);
                } else {
                    $reactions.answer("К сожалению, вакансии по вашему запросу не найдены.");
                }
            }).catch(function (err) {
                $reactions.answer("Что-то сервер барахлит. Не могу узнать погоду.");
            });
    
        state: NextPage
            q: *след*
            script:
                if ($session.page !== undefined && $session.vacancies) {
                    $session.page++;
                    if ($session.page * 3 < $session.vacancies.length) {
                        showPage($session.page, $session.vacancies);
                    } else {
                        $reactions.answer("Это последняя страница.");
                    }
                } else {
                    $reactions.answer("Сначала выполните поиск вакансий.");
                }
                
        state: PrevPage
            q: *пред*
            script:
                if ($session.page > 0) {
                    $session.page--;
                    showPage($session.page, $session.vacancies);
                } else {
                    $reactions.answer("Это первая страница.");
                }
               
               
                
                
    state: Bye
        intent!: /пока
        random:
            a: Всего доброго! Рад был помочь)
            a: До свидания! Надеюсь смог вам помочь
            a: Удачи! Рад что был вам полезен)



    state: NoMatch
        event!: noMatch
        random:
            a: Извините, я не понимаю. Переформулируйте, пожалуйста
            a: Простите, кажется я вас не понял. Напишите своё предложение иначе.
            a: К сожалению я не понял, что значит следующее сообщение: {{$request.query}}



    state: Profanity
        q!: * @mystem.obsc *
        random:
            a: Ваше сообщение содержит нецензурные слова. Пожалуйста, используйте более корректные выражения.
            a: Пожалуйста, избегайте нецензурных выражений. Мы стараемся поддерживать уважительную атмосферу.
            a: Давайте соблюдать нормы приличия и не использовать подобные выражения.

    state: ProfanityAlt
        q!: * @mlps-obscene.obscene *
        random:
            a: Ваше сообщение содержит нецензурные слова. Пожалуйста, используйте более корректные выражения.
            a: Пожалуйста, избегайте нецензурных выражений. Мы стараемся поддерживать уважительную атмосферу.
            a: Давайте соблюдать нормы приличия и не использовать подобные выражения.