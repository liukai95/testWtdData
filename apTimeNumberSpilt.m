clear;
clc;
n=245;
for pro=1:2  %�ļ��б��
    totalTime=zeros(n);
    totalNumber=zeros(n);
    for fil=1:297  %���ļ�����ӵõ����ļ�,297ΪAP��Ŀ����Ҫ���и���
        %*********************����apTime
        strTime= strcat ('F:\�ƶ��켣����\timeDivide\apVisited', int2str(pro) ,'\apTime\apTime_', int2str(fil) , '.csv') ; % �����ַ���
        mTime=load(strTime,'r');%��ֻ����ʽ���ļ�
        assignin('base',['x' num2str(fil)],mTime);
        %disp(['x' num2str(i)]);
        eval(['totalTime','=','totalTime','+','x',num2str(fil),';']);
        %*********************����apNumber
        strNumber= strcat ('F:\�ƶ��켣����\timeDivide\apVisited', int2str(pro) ,'\apNumber\apNumber_', int2str(fil) , '.csv') ; % �����ַ���
        mNumber=load(strNumber,'r');%��ֻ����ʽ���ļ�
        assignin('base',['y' num2str(fil)],mNumber);
        %disp(['y' num2str(i)]);
        eval(['totalNumber','=','totalNumber','+','y',num2str(fil),';'])

    end
    a=sum(sum(totalTime));
    b=sum(sum(totalNumber));
%             if(sum(totalTime(i,:)~=0))
%                      totalTimeRate(i,j) = totalTime(i,j)/sum(totalTime(i,:));%ÿ�����ݳ��������л��еĺͣ��õ�����
%             end
%             if(sum(totalNumber(i,:)~=0))
%                      totalNumberRate(i,j) = totalNumber(i,j)/sum(totalNumber(i,:));%ÿ�����ݳ��������л��еĺͣ��õ�����
%             end

    totalTimeRate= totalTime/a;
    totalNumberRate = totalNumber/b;
    totalRate=totalTimeRate+0.5*totalNumberRate; 
    
    strTime2= strcat ('F:\test\apTotalTime2', int2str(pro) , '.txt') ; % �ֶ�������ʱ�䣬���ĺ���ķֶ�����
    dlmwrite(strTime2, totalTime);  
    
    strNumber2= strcat ('F:\test\apTotalNumber2', int2str(pro) , '.txt') ; % �ֶ�����������
    dlmwrite(strNumber2, totalNumber);
    
    strTimeRate= strcat ('F:\test\apTotalTimeRate2', int2str(pro) , '.txt') ; % �ֶ�ʱ�����
    dlmwrite(strTimeRate, totalTimeRate); 
    
    strNumberRate= strcat ('F:\test\apTotalNumberRate2', int2str(pro) , '.txt') ; % �ֶδ�������
    dlmwrite(strNumberRate, totalNumberRate); 
    
    strRN= strcat ('F:\test\apTotalRate2', int2str(pro) , '.txt') ; % �ֶ��ܸ���
    dlmwrite(strRN, totalRate); 
end