clear;
clc;
n=245;
totalTime=zeros(n);
totalNumber=zeros(n);
    for fil=1:297  %���ļ�����ӵõ����ļ�,297ΪAP��Ŀ����Ҫ���и���
        %*********************����apTime
        strTime= strcat ('F:\�ƶ��켣����\apVisited\apTime\apTime_', int2str(fil) , '.csv') ; % �����ַ���
        mTime=load(strTime,'r');%��ֻ����ʽ���ļ�
        assignin('base',['x' num2str(fil)],mTime);
        %disp(['x' num2str(i)]);
        eval(['totalTime','=','totalTime','+','x',num2str(fil),';']);
        %*********************����apNumber
        strNumber= strcat ('F:\�ƶ��켣����\apVisited\apNumber\apNumber_', int2str(fil) , '.csv') ; % �����ַ���
        mNumber=load(strNumber,'r');%��ֻ����ʽ���ļ�
        assignin('base',['y' num2str(fil)],mNumber);
        %disp(['y' num2str(i)]);
        eval(['totalNumber','=','totalNumber','+','y',num2str(fil),';'])

    end
    a=sum(sum(totalTime));
    b=sum(sum(totalNumber));
        
    totalTimeRate = totalTime/sum(sum(totalTime));
    totalNumberRate = totalNumber/sum(sum(totalNumber));
    totalRate=totalTimeRate+0.5*totalNumberRate; 

    strTime2= strcat ('F:\test\apTotalTime.txt') ; % ������ʱ��
    dlmwrite(strTime2, totalTime);  
    
    strNumber2= strcat ('F:\test\apTotalNumber.txt') ; % ����������
    dlmwrite(strNumber2, totalNumber);
    
    strTimeRate= strcat ('F:\test\apTotalTimeRate.txt') ; % ʱ�����
    dlmwrite(strTimeRate, totalTimeRate);  
    
    strNumberRate= strcat ('F:\test\apTotalNumberRate.txt') ; % ��������
    dlmwrite(strNumberRate, totalNumberRate); 
    strRN= strcat ('F:\test\apTotalRate.txt') ; % �ܸ��ʣ�������
    dlmwrite(strRN, totalRate); 
    
    %rateConnection=load('F:\test\apTotalRate.txt');
    %rateConnection(rateConnection~=0) = 1;%�õ���Ȩ�ľ���ֻҪ��Ϊ0,���Ϊ1