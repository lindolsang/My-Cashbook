package kr.lindol.mycashbook.add;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Calendar;
import java.util.Date;

import kr.lindol.mycashbook.data.CashLogDataSource;
import kr.lindol.mycashbook.data.CashLogRepository;
import kr.lindol.mycashbook.data.db.CashLog;

public class CashLogAddPresenterTest {

    private CashLogRepository repository;
    private AddContract.View view;
    private ArgumentCaptor<CashLog> argumentCaptor;

    private CashLogAddPresenter presenter;

    @Before
    public void setUp() throws Exception {
        repository = mock(CashLogRepository.class);
        view = mock(AddContract.View.class);

        argumentCaptor = ArgumentCaptor.forClass(CashLog.class);

        presenter = new CashLogAddPresenter(repository, view);
    }

    @After
    public void tearDown() throws Exception {
    }

    private void mockSuccess() {
        doAnswer(invocation -> {
            CashLogDataSource.OperationCallback cb = invocation.getArgument(1);
            cb.onFinished();

            return null;
        }).when(repository).save(any(), any());
    }

    private Date getTime(int year, int month, int date) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, date);

        return cal.getTime();
    }

    @Test
    public void addAsIncomeThenSuccess() {
        mockSuccess();

        presenter.addAsIncome("Hello", "1000", new Date(), "description");

        verify(view, times(1)).showSuccess();
    }

    @Test
    public void addAsOutlayThenSuccess() {
        mockSuccess();

        presenter.addAsOutlay("Hello", "1000", new Date(), "description");

        verify(view, times(1)).showSuccess();
    }

    @Test
    public void addAsOutlayWithSpecifiedDateThenSave() {
        presenter.addAsOutlay("hello", "1000", getTime(2021, 0, 1), "description");

        verify(repository, times(1)).save(argumentCaptor.capture(), any());
        assertThat(argumentCaptor.getValue().monthTag, equalTo("202101"));
        assertThat(argumentCaptor.getValue().dayTag, equalTo("20210101"));
    }

    @Test
    public void addAsIncomeWithSpecifiedDateThenSave() {
        presenter.addAsIncome("hello", "1000", getTime(2021, 0, 1), "description");

        verify(repository, times(1)).save(argumentCaptor.capture(), any());
        assertThat(argumentCaptor.getValue().monthTag, equalTo("202101"));
        assertThat(argumentCaptor.getValue().dayTag, equalTo("20210101"));
    }

    @Test
    public void addAsOutlayThenSaveWithType1() {
        presenter.addAsOutlay("hello", "1000", new Date(), "description");

        verify(repository, times(1)).save(argumentCaptor.capture(), any());
        assertThat(argumentCaptor.getValue().type, equalTo(1));
    }

    @Test
    public void addAsIncomeThenSaveWithType0() {
        presenter.addAsIncome("hello", "1000", new Date(), "description");

        verify(repository, times(1)).save(argumentCaptor.capture(), any());
        assertThat(argumentCaptor.getValue().type, equalTo(0));
    }

    @Test
    public void addAsOutlayThenClearForms() {
        mockSuccess();

        presenter.addAsIncome("hello", "1000", new Date(), "description");

        verify(view, times(1)).clearForms();
    }

    @Test
    public void addAsIncomeThenClearForms() {
        mockSuccess();

        presenter.addAsOutlay("hello", "1000", new Date(), "description");

        verify(view, times(1)).clearForms();
    }

    @Test
    public void addAsIncomeWithEmptyItemThenShowItemValueEmptyError() {
        presenter.addAsIncome("", "1000", new Date(), "description");

        verify(view, times(1)).showItemValueEmptyError();
    }

    @Test
    public void addAsIncomeWithEmptyAmountThenShowAmountValueEmptyError() {
        presenter.addAsIncome("hello", "", new Date(), "description");

        verify(view, times(1)).showAmountValueEmptyError();
    }

    @Test
    public void addAsIncomeWithZeroAmountThenShowAmountValueSmallError() {
        presenter.addAsIncome("hello", "0", new Date(), "description");

        verify(view, times(1)).showAmountValueSmallError();
    }

    @Test
    public void addAsIncomeWithMinusAmountThenShowAmountValueSmallError() {
        presenter.addAsIncome("hello", "-1", new Date(), "description");

        verify(view, times(1)).showAmountValueSmallError();
    }

    @Test
    public void addAsIncomeWithAmountHasNonNumberCharThenShowAmountValueFormatError() {
        presenter.addAsIncome("hello", "10.0", new Date(), "description");

        verify(view, times(1)).showAmountValueFormatError();
    }

    @Test
    public void addAsOutlayWithEmptyItemThenShowItemValueEmptyError() {
        presenter.addAsOutlay("", "1000", new Date(), "description");

        verify(view, times(1)).showItemValueEmptyError();
    }

    @Test
    public void addAsOutlayWithEmptyAmountThenShowAmountValueEmptyError() {
        presenter.addAsOutlay("hello", "", new Date(), "description");

        verify(view, times(1)).showAmountValueEmptyError();
    }

    @Test
    public void addAsOutlayWithZeroAmountThenShowAmountValueSmallError() {
        presenter.addAsOutlay("hello", "0", new Date(), "description");

        verify(view, times(1)).showAmountValueSmallError();
    }

    @Test
    public void addAsOutlayWithMinusAmountThenShowAmountValueSmallError() {
        presenter.addAsOutlay("hello", "-1", new Date(), "description");

        verify(view, times(1)).showAmountValueSmallError();
    }

    @Test
    public void addAsOutlayWithAmountHasNonNumberCharThenShowAmountValueFormatError() {
        presenter.addAsOutlay("hello", "10.0", new Date(), "description");

        verify(view, times(1)).showAmountValueFormatError();
    }
}